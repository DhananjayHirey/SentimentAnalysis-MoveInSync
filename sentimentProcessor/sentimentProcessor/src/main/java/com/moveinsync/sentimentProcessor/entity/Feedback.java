package com.moveinsync.sentimentProcessor.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Feedback {


    private Long id;
    private String entityType;
    private String entityId;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;
}
