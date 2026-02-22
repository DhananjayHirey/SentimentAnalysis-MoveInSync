package com.moveinsync.sentimentProcessor.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public class FeedbackEvent {
    private String entityType; // DRIVER, TRIP, MARSHAL, APP
    private String entityId;
    private int rating; // 1-5
    private String comment;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime createdAt;

    public FeedbackEvent() {
    }

    public FeedbackEvent(String entityType, String entityId, int rating, String comment, LocalDateTime createdAt) {
        this.entityType = entityType;
        this.entityId = entityId;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    public static FeedbackEventBuilder builder() {
        return new FeedbackEventBuilder();
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public static class FeedbackEventBuilder {
        private String entityType;
        private String entityId;
        private int rating;
        private String comment;
        private LocalDateTime createdAt;

        public FeedbackEventBuilder entityType(String entityType) {
            this.entityType = entityType;
            return this;
        }

        public FeedbackEventBuilder entityId(String entityId) {
            this.entityId = entityId;
            return this;
        }

        public FeedbackEventBuilder rating(int rating) {
            this.rating = rating;
            return this;
        }

        public FeedbackEventBuilder comment(String comment) {
            this.comment = comment;
            return this;
        }

        public FeedbackEventBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public FeedbackEvent build() {
            return new FeedbackEvent(entityType, entityId, rating, comment, createdAt);
        }
    }
}
