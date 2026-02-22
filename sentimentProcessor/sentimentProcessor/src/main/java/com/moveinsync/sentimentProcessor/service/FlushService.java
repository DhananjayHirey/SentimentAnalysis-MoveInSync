package com.moveinsync.sentimentProcessor.service;

import com.moveinsync.sentimentProcessor.event.DriverAlertEvent;
import com.moveinsync.sentimentProcessor.repository.DriverSentimentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class FlushService {

    private final StringRedisTemplate redisTemplate;
    private final DriverSentimentRepository repository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Scheduled(fixedRate = 30000)
    public void flush() {

        Set<String> dirtyDrivers =
                redisTemplate.opsForSet().members("driver:dirty");

        if (dirtyDrivers == null) return;

        for (Object driverIdObj : dirtyDrivers) {

            String driverId = driverIdObj.toString();
            String key = "driver:sentiment:" + driverId;

            Object totalObj = redisTemplate.opsForHash()
                    .get(key, "total_score");

            Object countObj = redisTemplate.opsForHash()
                    .get(key, "feedback_count");

            if (totalObj == null || countObj == null) continue;

            double total = Double.parseDouble(totalObj.toString());
            int count = Integer.parseInt(countObj.toString());

            double avg = total / count;

            // 1️⃣ Upsert to DB
            repository.upsert(driverId, total, count, avg);

            // 2️⃣ Alert logic here
            handleAlert(driverId, avg, count);
        }

        redisTemplate.delete("driver:dirty");
    }

    private void handleAlert(String driverId, double avg, int count) {

        String alertKey = "driver:alerted:" + driverId;
        Boolean alreadyAlerted = redisTemplate.hasKey(alertKey);

        if (avg < 2.5 && !Boolean.TRUE.equals(alreadyAlerted)) {

            DriverAlertEvent alertEvent = DriverAlertEvent.builder()
                    .driverId(driverId)
                    .averageScore(avg)
                    .feedbackCount(count)
                    .triggeredAt(LocalDateTime.now())
                    .alertType("LOW_RATING")
                    .build();

            kafkaTemplate.send("driver-alert-events", driverId, alertEvent);

            redisTemplate.opsForValue().set(alertKey, "true");
        }

        // Optional: recovery alert
        if (avg >= 2.5 && Boolean.TRUE.equals(alreadyAlerted)) {

            DriverAlertEvent recoveryEvent = DriverAlertEvent.builder()
                    .driverId(driverId)
                    .averageScore(avg)
                    .feedbackCount(count)
                    .triggeredAt(LocalDateTime.now())
                    .alertType("RECOVERED")
                    .build();

            kafkaTemplate.send("driver-alert-events", driverId, recoveryEvent);

            redisTemplate.delete(alertKey);
        }
    }
}