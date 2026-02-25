package com.vishal.aiyoutube.topic_management_service.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        Map<String, Object> props = new HashMap<>();

        // FIXED: Uses the variable instead of hardcoded "localhost"
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "topic-service-group-v10");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        // Security & Trust Settings
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.vishal.aiyoutube.*");

        // Type Mappings for Cross-Service DTOs
        props.put(JsonDeserializer.TYPE_MAPPINGS,
                "com.vishal.aiyoutube.ai_analysis_service.dto.AnalysisCompletedEvent:com.vishal.aiyoutube.topic_management_service.dto.AnalysisCompletedEvent, " +
                        "com.vishal.aiyoutube.ai_analysis_service.dto.StatusUpdateEvent:com.vishal.aiyoutube.topic_management_service.dto.StatusUpdateEvent"
        );

        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}