package com.moveinsync.sentimentProcessor.entity;

import com.moveinsync.sentimentProcessor.analyzer.SentimentLabel;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "driver_feedback")
public class DriverFeedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "driver_id", nullable = false)
    private String driverId;

    @Column(nullable = false)
    private int rating;

    private String comment;

    @Enumerated(EnumType.STRING)
    @Column(name = "sentiment_label", nullable = false)
    private SentimentLabel sentimentLabel;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public DriverFeedback() {
    }

    public DriverFeedback(String driverId, int rating, String comment, SentimentLabel sentimentLabel,
            LocalDateTime createdAt) {
        this.driverId = driverId;
        this.rating = rating;
        this.comment = comment;
        this.sentimentLabel = sentimentLabel;
        this.createdAt = createdAt;
    }

    public static DriverFeedbackBuilder builder() {
        return new DriverFeedbackBuilder();
    }

    public Long getId() {
        return id;
    }

    public String getDriverId() {
        return driverId;
    }

    public int getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public SentimentLabel getSentimentLabel() {
        return sentimentLabel;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public static class DriverFeedbackBuilder {
        private String driverId;
        private int rating;
        private String comment;
        private SentimentLabel sentimentLabel;
        private LocalDateTime createdAt;

        public DriverFeedbackBuilder driverId(String driverId) {
            this.driverId = driverId;
            return this;
        }

        public DriverFeedbackBuilder rating(int rating) {
            this.rating = rating;
            return this;
        }

        public DriverFeedbackBuilder comment(String comment) {
            this.comment = comment;
            return this;
        }

        public DriverFeedbackBuilder sentimentLabel(SentimentLabel sentimentLabel) {
            this.sentimentLabel = sentimentLabel;
            return this;
        }

        public DriverFeedbackBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public DriverFeedback build() {
            return new DriverFeedback(driverId, rating, comment, sentimentLabel, createdAt);
        }
    }
}
