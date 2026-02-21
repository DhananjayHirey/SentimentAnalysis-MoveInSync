package com.moveinsync.feedbackService.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // entityType --> DRIVER, TRIP
    private String entityType;
    private String entityId;

    private int rating;
    private String comment;

    private LocalDateTime createdAt;
}
