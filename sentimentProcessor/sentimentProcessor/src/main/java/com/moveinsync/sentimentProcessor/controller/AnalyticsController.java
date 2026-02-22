package com.moveinsync.sentimentProcessor.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final StringRedisTemplate redisTemplate;

    @GetMapping("/drivers")
    public List<Map<String, Object>> getDriverSentiment() {
        Set<String> driverIds = redisTemplate.opsForSet().members("driver:active");
        if (driverIds == null)
            return List.of();

        List<Map<String, Object>> results = new ArrayList<>();
        for (String driverId : driverIds) {
            String key = "driver:metrics:" + driverId;
            Map<Object, Object> redisData = redisTemplate.opsForHash().entries(key);

            if (redisData.isEmpty())
                continue;

            double total = Double.parseDouble(redisData.getOrDefault("total_rating_sum", "0").toString());
            int count = Integer.parseInt(redisData.getOrDefault("feedback_count", "0").toString());
            int pos = Integer.parseInt(redisData.getOrDefault("positive_count", "0").toString());
            int neu = Integer.parseInt(redisData.getOrDefault("neutral_count", "0").toString());
            int neg = Integer.parseInt(metricsKeySuffix("negative", redisData)); // Helper for clarity

            double avg = count == 0 ? 0.0 : total / count;

            results.add(Map.of(
                    "driverId", driverId,
                    "averageScore", avg,
                    "feedbackCount", count,
                    "positiveCount", pos,
                    "neutralCount", neu,
                    "negativeCount", neg,
                    "isLive", true));
        }
        return results;
    }

    @GetMapping("/summary")
    public Map<String, Object> getSummary() {
        Long totalDrivers = redisTemplate.opsForSet().size("driver:active");
        Long activeAlerts = redisTemplate.opsForSet().size("alerts:active");
        long totalFeedbacks = getLong("analytics:total_feedbacks");
        double totalRatingSum = getDouble("analytics:system_rating_sum");

        double avgSystemSentiment = totalFeedbacks == 0 ? 0.0 : totalRatingSum / totalFeedbacks;

        return Map.of(
                "totalDrivers", totalDrivers != null ? totalDrivers : 0,
                "totalFeedbacks", totalFeedbacks,
                "averageSystemSentiment", avgSystemSentiment,
                "activeAlerts", activeAlerts != null ? activeAlerts : 0,
                "sentimentBreakdown", Map.of(
                        "positive", getLong("analytics:positive_total"),
                        "neutral", getLong("analytics:neutral_total"),
                        "negative", getLong("analytics:negative_total")));
    }

    private String metricsKeySuffix(String suffix, Map<Object, Object> data) {
        return data.getOrDefault(suffix + "_count", "0").toString();
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
