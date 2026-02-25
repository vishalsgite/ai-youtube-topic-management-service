package com.vishal.aiyoutube.topic_management_service.config;

import com.vishal.aiyoutube.topic_management_service.dto.TopicSubmittedEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public NewTopic topicSubmittedTopic() {
        return TopicBuilder.name("topic-submitted-events")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public ProducerFactory<String, TopicSubmittedEvent> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();

        // FIXED: Uses the variable instead of hardcoded "localhost"
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, TopicSubmittedEvent> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}