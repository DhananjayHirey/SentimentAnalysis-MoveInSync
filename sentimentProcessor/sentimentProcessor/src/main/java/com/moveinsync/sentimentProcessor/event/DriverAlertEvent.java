package com.moveinsync.sentimentProcessor.event;

import java.time.LocalDateTime;

public class DriverAlertEvent {
    private String driverId;
    private double averageScore;
    private int feedbackCount;
    private LocalDateTime triggeredAt;
    private String alertType;

    public DriverAlertEvent() {
    }

    public DriverAlertEvent(String driverId, double averageScore, int feedbackCount, LocalDateTime triggeredAt,
            String alertType) {
        this.driverId = driverId;
        this.averageScore = averageScore;
        this.feedbackCount = feedbackCount;
        this.triggeredAt = triggeredAt;
        this.alertType = alertType;
    }

    public static DriverAlertEventBuilder builder() {
        return new DriverAlertEventBuilder();
    }

    public String getDriverId() {
        return driverId;
    }

    public double getAverageScore() {
        return averageScore;
    }

    public int getFeedbackCount() {
        return feedbackCount;
    }

    public LocalDateTime getTriggeredAt() {
        return triggeredAt;
    }

    public String getAlertType() {
        return alertType;
    }

    public static class DriverAlertEventBuilder {
        private String driverId;
        private double averageScore;
        private int feedbackCount;
        private LocalDateTime triggeredAt;
        private String alertType;

        public DriverAlertEventBuilder driverId(String driverId) {
            this.driverId = driverId;
            return this;
        }

        public DriverAlertEventBuilder averageScore(double averageScore) {
            this.averageScore = averageScore;
            return this;
        }

        public DriverAlertEventBuilder feedbackCount(int feedbackCount) {
            this.feedbackCount = feedbackCount;
            return this;
        }

        public DriverAlertEventBuilder triggeredAt(LocalDateTime triggeredAt) {
            this.triggeredAt = triggeredAt;
            return this;
        }

        public DriverAlertEventBuilder alertType(String alertType) {
            this.alertType = alertType;
            return this;
        }

        public DriverAlertEvent build() {
            return new DriverAlertEvent(driverId, averageScore, feedbackCount, triggeredAt, alertType);
        }
    }
}