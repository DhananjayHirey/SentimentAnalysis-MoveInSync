package com.moveinsync.sentimentProcessor.controller;

import com.moveinsync.sentimentProcessor.repository.DriverSentimentRepository;
import com.moveinsync.sentimentProcessor.repository.TripSentimentRepository;
import com.moveinsync.sentimentProcessor.repository.MarshalSentimentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final StringRedisTemplate redisTemplate;
    private final DriverSentimentRepository driverSentimentRepo;
    private final TripSentimentRepository tripSentimentRepo;
    private final MarshalSentimentRepository marshalSentimentRepo;

    /**
     * Returns DB-backed driver sentiment summaries (updated every 30s by
     * FlushService).
     */
    @GetMapping("/drivers")
    public List<Map<String, Object>> getDriverSentiment() {
        return driverSentimentRepo.findAll().stream()
                .map(d -> Map.<String, Object>of(
                        "driverId", d.getDriverId(),
                        "averageScore", d.getAverageRating(),
                        "feedbackCount", d.getFeedbackCount(),
                        "positiveCount", d.getPositiveCount(),
                        "neutralCount", d.getNeutralCount(),
                        "negativeCount", d.getNegativeCount()))
                .toList();
    }

    /**
     * Returns DB-backed trip sentiment summaries (updated every 30s by
     * FlushService).
     */
    @GetMapping("/trips")
    public List<Map<String, Object>> getTripSentiment() {
        return tripSentimentRepo.findAll().stream()
                .map(t -> Map.<String, Object>of(
                        "tripId", t.getTripId(),
                        "averageScore", t.getAverageRating(),
                        "feedbackCount", t.getFeedbackCount(),
                        "positiveCount", t.getPositiveCount(),
                        "neutralCount", t.getNeutralCount(),
                        "negativeCount", t.getNegativeCount()))
                .toList();
    }

    /**
     * Returns DB-backed marshal sentiment summaries (updated every 30s by
     * FlushService).
     */
    @GetMapping("/marshals")
    public List<Map<String, Object>> getMarshalSentiment() {
        return marshalSentimentRepo.findAll().stream()
                .map(m -> Map.<String, Object>of(
                        "marshalId", m.getMarshalId(),
                        "averageScore", m.getAverageRating(),
                        "feedbackCount", m.getFeedbackCount(),
                        "positiveCount", m.getPositiveCount(),
                        "neutralCount", m.getNeutralCount(),
                        "negativeCount", m.getNegativeCount()))
                .toList();
    }

    /**
     * Summary endpoint: real-time counters from Redis, but entity counts from DB.
     */
    @GetMapping("/summary")
    public Map<String, Object> getSummary() {
        long totalDrivers = driverSentimentRepo.count();
        long totalTrips = tripSentimentRepo.count();
        long totalMarshals = marshalSentimentRepo.count();
        Long activeAlerts = redisTemplate.opsForSet().size("alerts:active");
        long totalFeedbacks = getLong("analytics:total_feedbacks");
        double totalRatingSum = getDouble("analytics:system_rating_sum");

        double avgSystemSentiment = totalFeedbacks == 0 ? 0.0 : totalRatingSum / totalFeedbacks;

        return Map.of(
                "totalDrivers", totalDrivers,
                "totalTrips", totalTrips,
                "totalMarshals", totalMarshals,
                "totalFeedbacks", totalFeedbacks,
                "averageSystemSentiment", avgSystemSentiment,
                "activeAlerts", activeAlerts != null ? activeAlerts : 0,
                "sentimentBreakdown", Map.of(
                        "positive", getLong("analytics:positive_total"),
                        "neutral", getLong("analytics:neutral_total"),
                        "negative", getLong("analytics:negative_total")));
    }

    private long getLong(String key) {
        String val = redisTemplate.opsForValue().get(key);
        return val == null ? 0L : Long.parseLong(val);
    }

    private double getDouble(String key) {
        String val = redisTemplate.opsForValue().get(key);
        return val == null ? 0.0 : Double.parseDouble(val);
    }
}
