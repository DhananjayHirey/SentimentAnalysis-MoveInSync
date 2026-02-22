package com.moveinsync.sentimentProcessor.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "driver_sentiment")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverSentiment {

    @Id
    private String driverId;

    private double totalScore;
    private int feedbackCount;
    private double averageScore;

    private LocalDateTime updatedAt;
}