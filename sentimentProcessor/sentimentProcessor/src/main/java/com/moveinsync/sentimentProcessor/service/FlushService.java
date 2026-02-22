package com.moveinsync.sentimentProcessor.service;

import com.moveinsync.sentimentProcessor.event.DriverAlertEvent;
import com.moveinsync.sentimentProcessor.repository.DriverSentimentRepository;
import com.moveinsync.sentimentProcessor.repository.MarshalSentimentRepository;
import com.moveinsync.sentimentProcessor.repository.TripSentimentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class FlushService {

    private final StringRedisTemplate redisTemplate;
    private final DriverSentimentRepository driverRepository;
    private final TripSentimentRepository tripRepository;
    private final MarshalSentimentRepository marshalRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Value("${alert.threshold:2.5}")
    private double alertThreshold;

    @Value("${alert.minFeedback:1}")
    private int minFeedback;

    @Scheduled(fixedDelay = 30000)
    public void flush() {
        flushDrivers();
        flushTrips();
        flushMarshals();
    }

    private void flushDrivers() {
        Set<String> dirtyDrivers = redisTemplate.opsForSet().members("driver:dirty");
        if (dirtyDrivers == null || dirtyDrivers.isEmpty())
            return;

        for (String driverId : dirtyDrivers) {
            Map<Object, Object> metrics = redisTemplate.opsForHash().entries("driver:metrics:" + driverId);
            if (metrics.isEmpty())
                continue;

            double totalSum = Double.parseDouble(metrics.getOrDefault("total_rating_sum", "0").toString());
            int count = Integer.parseInt(metrics.getOrDefault("feedback_count", "0").toString());
            int pos = Integer.parseInt(metrics.getOrDefault("positive_count", "0").toString());
            int neu = Integer.parseInt(metrics.getOrDefault("neutral_count", "0").toString());
            int neg = Integer.parseInt(metrics.getOrDefault("negative_count", "0").toString());

            if (count == 0)
                continue;
            double avg = totalSum / count;

            driverRepository.upsert(driverId, totalSum, count, avg, pos, neu, neg);
            handleAlert(driverId, avg, count);
        }
        redisTemplate.delete("driver:dirty");
    }

    private void flushTrips() {
        Set<String> dirtyTrips = redisTemplate.opsForSet().members("trip:dirty");
        if (dirtyTrips == null || dirtyTrips.isEmpty())
            return;

        for (String tripId : dirtyTrips) {
            Map<Object, Object> metrics = redisTemplate.opsForHash().entries("trip:metrics:" + tripId);
            if (metrics.isEmpty())
                continue;

            double totalSum = Double.parseDouble(metrics.getOrDefault("total_rating_sum", "0").toString());
            int count = Integer.parseInt(metrics.getOrDefault("feedback_count", "0").toString());
            int pos = Integer.parseInt(metrics.getOrDefault("positive_count", "0").toString());
            int neu = Integer.parseInt(metrics.getOrDefault("neutral_count", "0").toString());
            int neg = Integer.parseInt(metrics.getOrDefault("negative_count", "0").toString());

            if (count == 0)
                continue;
            tripRepository.upsert(tripId, totalSum, count, avg(totalSum, count), pos, neu, neg);
        }
        redisTemplate.delete("trip:dirty");
    }

    private void flushMarshals() {
        Set<String> dirtyMarshals = redisTemplate.opsForSet().members("marshal:dirty");
        if (dirtyMarshals == null || dirtyMarshals.isEmpty())
            return;

        for (String marshalId : dirtyMarshals) {
            Map<Object, Object> metrics = redisTemplate.opsForHash().entries("marshal:metrics:" + marshalId);
            if (metrics.isEmpty())
                continue;

            double totalSum = Double.parseDouble(metrics.getOrDefault("total_rating_sum", "0").toString());
            int count = Integer.parseInt(metrics.getOrDefault("feedback_count", "0").toString());
            int pos = Integer.parseInt(metrics.getOrDefault("positive_count", "0").toString());
            int neu = Integer.parseInt(metrics.getOrDefault("neutral_count", "0").toString());
            int neg = Integer.parseInt(metrics.getOrDefault("negative_count", "0").toString());

            if (count == 0)
                continue;
            marshalRepository.upsert(marshalId, totalSum, count, avg(totalSum, count), pos, neu, neg);
        }
        redisTemplate.delete("marshal:dirty");
    }

    private double avg(double sum, int count) {
        return count == 0 ? 0.0 : sum / count;
    }

    private void handleAlert(String driverId, double avg, int count) {
        String alertStateKey = "driver:alerted:" + driverId;
        boolean currentlyAlerted = Boolean.TRUE.equals(redisTemplate.hasKey(alertStateKey));

        if (avg < alertThreshold && count >= minFeedback) {
            if (!currentlyAlerted) {
                sendAlert(driverId, avg, count, "LOW_RATING");
                redisTemplate.opsForValue().set(alertStateKey, "true");
                redisTemplate.opsForSet().add("alerts:active", driverId);
            }
        } else {
            if (currentlyAlerted) {
                sendAlert(driverId, avg, count, "RECOVERED");
                redisTemplate.delete(alertStateKey);
                redisTemplate.opsForSet().remove("alerts:active", driverId);
            }
        }
    }

    private void sendAlert(String driverId, double avg, int count, String type) {
        DriverAlertEvent alertEvent = DriverAlertEvent.builder()
                .driverId(driverId)
                .averageScore(avg)
                .feedbackCount(count)
                .triggeredAt(LocalDateTime.now())
                .alertType(type)
                .build();

        messagingTemplate.convertAndSend("/topic/alerts", alertEvent);
        System.out.println("Alert " + type + " sent for driver: " + driverId);
    }
}