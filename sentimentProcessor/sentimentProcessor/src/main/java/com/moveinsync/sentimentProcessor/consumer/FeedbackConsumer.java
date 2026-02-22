package com.moveinsync.sentimentProcessor.consumer;

import com.moveinsync.sentimentProcessor.analyzer.SentimentAnalyzer;
import com.moveinsync.sentimentProcessor.entity.Feedback;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeedbackConsumer {

    private final SentimentAnalyzer analyzer;
    private final StringRedisTemplate redisTemplate;

    @KafkaListener(topics = "feedback-events", groupId = "sentiment-group")
    public void consume(Feedback feedback) {
        System.out.println("event consumed: " + feedback.getComment());

        String effectiveId = feedback.getDriverId() != null ? feedback.getDriverId() : feedback.getEntityId();
        if (effectiveId == null) {
            System.out.println("Skipping feedback with missing ID");
            return;
        }

        double score = analyzer.analyze(
                feedback.getComment(),
                feedback.getRating());

        String key = "driver:sentiment:" + effectiveId;

        redisTemplate.opsForHash().increment(key, "total_score", score);
        redisTemplate.opsForHash().increment(key, "feedback_count", 1);
        redisTemplate.opsForValue().increment("analytics:total_feedbacks", 1);
        redisTemplate.opsForValue().increment("analytics:system_total_score", score);

        redisTemplate.opsForSet().add("driver:dirty", effectiveId);
        redisTemplate.opsForSet().add("driver:active", effectiveId);
    }
}