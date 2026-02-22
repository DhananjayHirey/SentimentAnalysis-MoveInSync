package com.moveinsync.sentimentProcessor.entity;

import com.moveinsync.sentimentProcessor.analyzer.SentimentLabel;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "trip_feedback")
public class TripFeedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "trip_id", nullable = false)
    private String tripId;

    @Column(nullable = false)
    private int rating;

    private String comment;

    @Enumerated(EnumType.STRING)
    @Column(name = "sentiment_label", nullable = false)
    private SentimentLabel sentimentLabel;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public TripFeedback() {
    }

    public TripFeedback(String tripId, int rating, String comment, SentimentLabel sentimentLabel,
            LocalDateTime createdAt) {
        this.tripId = tripId;
        this.rating = rating;
        this.comment = comment;
        this.sentimentLabel = sentimentLabel;
        this.createdAt = createdAt;
    }

    public static TripFeedbackBuilder builder() {
        return new TripFeedbackBuilder();
    }

    public Long getId() {
        return id;
    }

    public String getTripId() {
        return tripId;
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

    public static class TripFeedbackBuilder {
        private String tripId;
        private int rating;
        private String comment;
        private SentimentLabel sentimentLabel;
        private LocalDateTime createdAt;

        public TripFeedbackBuilder tripId(String tripId) {
            this.tripId = tripId;
            return this;
        }

        public TripFeedbackBuilder rating(int rating) {
            this.rating = rating;
            return this;
        }

        public TripFeedbackBuilder comment(String comment) {
            this.comment = comment;
            return this;
        }

        public TripFeedbackBuilder sentimentLabel(SentimentLabel sentimentLabel) {
            this.sentimentLabel = sentimentLabel;
            return this;
        }

        public TripFeedbackBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public TripFeedback build() {
            return new TripFeedback(tripId, rating, comment, sentimentLabel, createdAt);
        }
    }
}
