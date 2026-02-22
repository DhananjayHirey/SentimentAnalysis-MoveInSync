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

            String key = "driver:sentiment:" + driverId;
            Map<Object, Object> redisData = redisTemplate.opsForHash().entries(key);

            if (redisData.isEmpty())
                continue;

            double total = Double.parseDouble(redisData.get("total_score").toString());
            int count = Integer.parseInt(redisData.get("feedback_count").toString());
            double avg = total / count;

            results.add(Map.of(
                    "driverId", driverId,
                    "averageScore", avg,
                    "feedbackCount", count,
                    "isLive", true));
        }

        return results;
    }

    @GetMapping("/summary")
    public Map<String, Object> getSummary() {

        Long totalDrivers = redisTemplate.opsForSet().size("driver:active");
        Long activeAlerts = redisTemplate.opsForSet().size("alerts:active");
        long totalFeedbacks = getLong("analytics:total_feedbacks");
        double totalScore = getDouble("analytics:system_total_score");

        double avgSystemSentiment = totalFeedbacks == 0 ? 0.0 : totalScore / totalFeedbacks;

        return Map.of(
                "totalDrivers", totalDrivers,
                "totalFeedbacks", totalFeedbacks,
                "averageSystemSentiment", avgSystemSentiment,
                "activeAlerts", activeAlerts);
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
