package com.moveinsync.feedbackService.controller;

import com.moveinsync.feedbackService.dto.FeedbackRequest;
import com.moveinsync.feedbackService.event.FeedbackEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
@RequestMapping("/api/feedback")
public class FeedbackController {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final Set<String> ALLOWED_TYPES = Set.of("DRIVER", "TRIP", "MARSHAL", "APP");

    @PostMapping
    public ResponseEntity<?> submit(@RequestBody FeedbackRequest request) {
        String type = request.getEntityType() != null ? request.getEntityType().toUpperCase() : null;
        String id = request.getEntityId();
        int rating = request.getRating();
        String comment = request.getComment();

        // 1. Validate
        if (type == null || !ALLOWED_TYPES.contains(type)) {
            return ResponseEntity.badRequest().body("Invalid entity type. Must be one of: " + ALLOWED_TYPES);
        }
        if (id == null || id.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Entity ID is required");
        }
        if (rating < 1 || rating > 5) {
            return ResponseEntity.badRequest().body("Rating must be between 1 and 5");
        }

        // 2. Map & Publish
        FeedbackEvent event = FeedbackEvent.builder()
                .entityType(type)
                .entityId(id)
                .rating(rating)
                .comment(comment)
                .createdAt(LocalDateTime.now())
                .build();

        kafkaTemplate.send("feedback-events", id, event);

        return ResponseEntity.ok("Feedback submitted successfully");
    }

   
}
