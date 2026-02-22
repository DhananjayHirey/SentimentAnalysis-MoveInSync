package com.moveinsync.sentimentProcessor.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "marshal_sentiment")
public class MarshalSentiment {
    @Id
    @Column(name = "marshal_id")
    private String marshalId;

    @Column(name = "total_rating_sum", nullable = false)
    private double totalRatingSum;

    @Column(name = "feedback_count", nullable = false)
    private int feedbackCount;

    @Column(name = "average_rating", nullable = false)
    private double averageRating;

    @Column(name = "positive_count", nullable = false)
    private int positiveCount;

    @Column(name = "neutral_count", nullable = false)
    private int neutralCount;

    @Column(name = "negative_count", nullable = false)
    private int negativeCount;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public MarshalSentiment() {
    }

    public String getMarshalId() {
        return marshalId;
    }

    public void setMarshalId(String marshalId) {
        this.marshalId = marshalId;
    }

    public double getTotalRatingSum() {
        return totalRatingSum;
    }

    public void setTotalRatingSum(double totalRatingSum) {
        this.totalRatingSum = totalRatingSum;
    }

    public int getFeedbackCount() {
        return feedbackCount;
    }

    public void setFeedbackCount(int feedbackCount) {
        this.feedbackCount = feedbackCount;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public int getPositiveCount() {
        return positiveCount;
    }

    public void setPositiveCount(int positiveCount) {
        this.positiveCount = positiveCount;
    }

    public int getNeutralCount() {
        return neutralCount;
    }

    public void setNeutralCount(int neutralCount) {
        this.neutralCount = neutralCount;
    }

    public int getNegativeCount() {
        return negativeCount;
    }

    public void setNegativeCount(int negativeCount) {
        this.negativeCount = negativeCount;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
