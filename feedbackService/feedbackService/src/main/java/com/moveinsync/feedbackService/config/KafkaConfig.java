package com.moveinsync.feedbackService.config;


import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {
    public NewTopic feedbackTopic(){
        return TopicBuilder.name("feedback-events").partitions(3).replicas(1).build();
    }
}
