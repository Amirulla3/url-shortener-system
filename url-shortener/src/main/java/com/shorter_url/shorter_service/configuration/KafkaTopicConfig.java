package com.shorter_url.shorter_service.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic linkClicksTopic(){
        return TopicBuilder
                .name("link-clicks")
                .partitions(3)
                .replicas(1)
                .build();
    }
}
