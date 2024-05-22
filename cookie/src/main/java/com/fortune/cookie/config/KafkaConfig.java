package com.fortune.cookie.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;

public class KafkaConfig {
    @Bean
    public NewTopic topic1() {
        return TopicBuilder.name("cookie-request")
                .compact()
                .build();
    }
}
