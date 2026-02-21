package com.moveinsync.feedbackService.dto;


import lombok.Data;

@Data
public class FeedbackRequest {
    private String entityType;
    private String entityId;
    private int rating;
    private String comment;
}
