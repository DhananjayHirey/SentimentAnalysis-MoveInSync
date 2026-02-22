package com.moveinsync.sentimentProcessor.consumer;

import com.moveinsync.sentimentProcessor.analyzer.SentimentAnalyzer;
import com.moveinsync.sentimentProcessor.analyzer.SentimentLabel;
import com.moveinsync.sentimentProcessor.entity.*;
import com.moveinsync.sentimentProcessor.event.FeedbackEvent;
import com.moveinsync.sentimentProcessor.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FeedbackConsumer {

    private final SentimentAnalyzer analyzer;
    private final StringRedisTemplate redisTemplate;
    private final SimpMessagingTemplate messagingTemplate;

    private final DriverFeedbackRepository driverFeedbackRepository;
    private final TripFeedbackRepository tripFeedbackRepository;
    private final MarshalFeedbackRepository marshalFeedbackRepository;
    private final AppFeedbackRepository appFeedbackRepository;

    @KafkaListener(topics = "feedback-events", groupId = "sentiment-group")
    public void consume(FeedbackEvent event) {
        System.out.println("Processing " + event.getEntityType() + " feedback for entity: " + event.getEntityId());

        try {
            // 1. Classify sentiment (Text only)
            SentimentLabel sentiment = analyzer.analyze(event.getComment());

            // 2. Persist to PostgreSQL
            persistRawFeedback(event, sentiment);

            // 3. Update Redis metrics
            updateRedisMetrics(event, sentiment);

            // 4. Broadcast via WebSocket (for real-time dashboard updates)
            broadcastUpdate(event);

        } catch (Exception e) {
            System.err.println("Error processing feedback event: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void persistRawFeedback(FeedbackEvent event, SentimentLabel sentiment) {
        String type = event.getEntityType().toUpperCase();
        switch (type) {
            case "DRIVER":
                driverFeedbackRepository.save(DriverFeedback.builder()
                        .driverId(event.getEntityId())
                        .rating(event.getRating())
                        .comment(event.getComment())
                        .sentimentLabel(sentiment)
                        .createdAt(event.getCreatedAt())
                        .build());
                break;
            case "TRIP":
                tripFeedbackRepository.save(TripFeedback.builder()
                        .tripId(event.getEntityId())
                        .rating(event.getRating())
                        .comment(event.getComment())
                        .sentimentLabel(sentiment)
                        .createdAt(event.getCreatedAt())
                        .build());
                break;
            case "MARSHAL":
                marshalFeedbackRepository.save(MarshalFeedback.builder()
                        .marshalId(event.getEntityId())
                        .rating(event.getRating())
                        .comment(event.getComment())
                        .sentimentLabel(sentiment)
                        .createdAt(event.getCreatedAt())
                        .build());
                break;
            case "APP":
                appFeedbackRepository.save(AppFeedback.builder()
                        .userId(event.getEntityId())
                        .rating(event.getRating())
                        .comment(event.getComment())
                        .sentimentLabel(sentiment)
                        .createdAt(event.getCreatedAt())
                        .build());
                break;
        }
    }

    private void updateRedisMetrics(FeedbackEvent event, SentimentLabel sentiment) {
        String type = event.getEntityType().toUpperCase();
        String id = event.getEntityId();
        int rating = event.getRating();
        String sentLabel = sentiment.name().toLowerCase();
        String sentKey = sentLabel + "_count";

        String metricsKey = type.toLowerCase() + ":metrics:" + id;
        redisTemplate.opsForHash().increment(metricsKey, "total_rating_sum", rating);
        redisTemplate.opsForHash().increment(metricsKey, "feedback_count", 1);
        redisTemplate.opsForHash().increment(metricsKey, sentKey, 1);

        if (event.getEntityType().equals("DRIVER")) {
            redisTemplate.opsForSet().add("driver:active", event.getEntityId());
            redisTemplate.opsForSet().add("driver:dirty", event.getEntityId());
        } else if (event.getEntityType().equals("TRIP")) {
            redisTemplate.opsForSet().add("trip:active", event.getEntityId());
            redisTemplate.opsForSet().add("trip:dirty", event.getEntityId());
        } else if (event.getEntityType().equals("MARSHAL")) {
            redisTemplate.opsForSet().add("marshal:active", event.getEntityId());
            redisTemplate.opsForSet().add("marshal:dirty", event.getEntityId());
        }

        // Global counters
        redisTemplate.opsForValue().increment("analytics:total_feedbacks", 1);
        redisTemplate.opsForValue().increment("analytics:system_rating_sum", rating);
        redisTemplate.opsForValue().increment("analytics:" + sentLabel + "_total", 1);
    }

    private void broadcastUpdate(FeedbackEvent event) {
        // Send a generic update notification to trigger dashboard refresh
        Map<String, Object> updatePayload = new HashMap<>();
        updatePayload.put("type", event.getEntityType());
        updatePayload.put("id", event.getEntityId());
        updatePayload.put("timestamp", System.currentTimeMillis());
        messagingTemplate.convertAndSend((String) "/topic/updates", (Object) updatePayload);
    }
}