package com.moveinsync.sentimentProcessor.entity;

import com.moveinsync.sentimentProcessor.analyzer.SentimentLabel;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "marshal_feedback")
public class MarshalFeedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "marshal_id", nullable = false)
    private String marshalId;

    @Column(nullable = false)
    private int rating;

    private String comment;

    @Enumerated(EnumType.STRING)
    @Column(name = "sentiment_label", nullable = false)
    private SentimentLabel sentimentLabel;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public MarshalFeedback() {
    }

    public MarshalFeedback(String marshalId, int rating, String comment, SentimentLabel sentimentLabel,
            LocalDateTime createdAt) {
        this.marshalId = marshalId;
        this.rating = rating;
        this.comment = comment;
        this.sentimentLabel = sentimentLabel;
        this.createdAt = createdAt;
    }

    public static MarshalFeedbackBuilder builder() {
        return new MarshalFeedbackBuilder();
    }

    public Long getId() {
        return id;
    }

    public String getMarshalId() {
        return marshalId;
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

    public static class MarshalFeedbackBuilder {
        private String marshalId;
        private int rating;
        private String comment;
        private SentimentLabel sentimentLabel;
        private LocalDateTime createdAt;

        public MarshalFeedbackBuilder marshalId(String marshalId) {
            this.marshalId = marshalId;
            return this;
        }

        public MarshalFeedbackBuilder rating(int rating) {
            this.rating = rating;
            return this;
        }

        public MarshalFeedbackBuilder comment(String comment) {
            this.comment = comment;
            return this;
        }

        public MarshalFeedbackBuilder sentimentLabel(SentimentLabel sentimentLabel) {
            this.sentimentLabel = sentimentLabel;
            return this;
        }

        public MarshalFeedbackBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public MarshalFeedback build() {
            return new MarshalFeedback(marshalId, rating, comment, sentimentLabel, createdAt);
        }
    }
}
