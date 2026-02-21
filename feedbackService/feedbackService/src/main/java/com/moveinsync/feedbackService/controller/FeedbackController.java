package com.moveinsync.feedbackService.controller;


import com.moveinsync.feedbackService.dto.FeedbackRequest;
import com.moveinsync.feedbackService.entity.Feedback;
import com.moveinsync.feedbackService.repository.FeedbackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/feedback")
public class FeedbackController {

    private final FeedbackRepository repository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @PostMapping
    public ResponseEntity<?> submit(@RequestBody FeedbackRequest request) {

        Feedback feedback = Feedback.builder()
                .entityType(request.getEntityType())
                .entityId(request.getEntityId())
                .rating(request.getRating())
                .comment(request.getComment())
                .createdAt(LocalDateTime.now())
                .build();

        repository.save(feedback);

        kafkaTemplate.send(
                "feedback-events",
                feedback.getEntityId(),
                feedback
        );

        return ResponseEntity.ok("Feedback submitted successfully");
    }
}