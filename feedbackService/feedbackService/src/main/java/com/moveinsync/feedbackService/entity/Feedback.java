package com.moveinsync.feedbackService.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "feedback")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // entityType --> DRIVER, TRIP
    @Column(name = "entity_type", nullable = false)
    private String entityType;

    @Column(name="entity_id", nullable = false)
    private String entityId;

    @Column(nullable = false)
    private int rating;


    private String comment;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
