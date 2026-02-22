package com.moveinsync.sentimentProcessor.event;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverAlertEvent {

    private String driverId;

    private double averageScore;

    private int feedbackCount;

    private LocalDateTime triggeredAt;

    private String alertType; // LOW_RATING, RECOVERED etc
}