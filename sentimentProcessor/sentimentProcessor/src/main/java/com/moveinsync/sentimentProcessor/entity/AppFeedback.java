package com.moveinsync.sentimentProcessor.entity;

import com.moveinsync.sentimentProcessor.analyzer.SentimentLabel;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "app_feedback")
public class AppFeedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(nullable = false)
    private int rating;

    private String comment;

    @Enumerated(EnumType.STRING)
    @Column(name = "sentiment_label", nullable = false)
    private SentimentLabel sentimentLabel;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public AppFeedback() {
    }

    public AppFeedback(String userId, int rating, String comment, SentimentLabel sentimentLabel,
            LocalDateTime createdAt) {
        this.userId = userId;
        this.rating = rating;
        this.comment = comment;
        this.sentimentLabel = sentimentLabel;
        this.createdAt = createdAt;
    }

    public static AppFeedbackBuilder builder() {
        return new AppFeedbackBuilder();
    }

    public Long getId() {
        return id;
    }

    public String getUserId() {
        return userId;
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

    public static class AppFeedbackBuilder {
        private String userId;
        private int rating;
        private String comment;
        private SentimentLabel sentimentLabel;
        private LocalDateTime createdAt;

        public AppFeedbackBuilder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public AppFeedbackBuilder rating(int rating) {
            this.rating = rating;
            return this;
        }

        public AppFeedbackBuilder comment(String comment) {
            this.comment = comment;
            return this;
        }

        public AppFeedbackBuilder sentimentLabel(SentimentLabel sentimentLabel) {
            this.sentimentLabel = sentimentLabel;
            return this;
        }

        public AppFeedbackBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public AppFeedback build() {
            return new AppFeedback(userId, rating, comment, sentimentLabel, createdAt);
        }
    }
}
