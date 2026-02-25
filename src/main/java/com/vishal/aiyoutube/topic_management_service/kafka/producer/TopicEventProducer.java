package com.vishal.aiyoutube.topic_management_service.kafka.producer;

import com.vishal.aiyoutube.topic_management_service.dto.TopicSubmittedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * Kafka Producer service responsible for broadcasting new research requests.
 * This class triggers the start of the multi-agent pipeline by notifying
 * the YouTube Processing Service to begin transcript extraction.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TopicEventProducer {

    /**
     * Spring Kafka template configured for TopicSubmittedEvent payloads.
     */
    private final KafkaTemplate<String, TopicSubmittedEvent> kafkaTemplate;

    /**
     * The target Kafka topic name for initial submissions.
     */
    private static final String TOPIC_NAME = "topic-submitted-events";

    /**
     * Publishes a TopicSubmittedEvent to Kafka asynchronously.
     * * @param event The DTO containing the topic UUID and the SEO-normalized query.
     */
    public void sendTopicSubmittedEvent(TopicSubmittedEvent event) {
        log.info("Attempting to publish TopicSubmittedEvent for ID: {}", event.getTopicId());

        /**
         * PARTITIONING STRATEGY:
         * We use the 'topicId' as the Kafka Message Key.
         * This ensures that all events related to the same research topic are
         * routed to the same Kafka partition, preserving message order if needed.
         */
        CompletableFuture<SendResult<String, TopicSubmittedEvent>> future =
                kafkaTemplate.send(TOPIC_NAME, event.getTopicId().toString(), event);

        /**
         * ASYNCHRONOUS CALLBACK:
         * We use CompletableFuture to handle the result of the produce request
         * without blocking the main execution thread.
         */
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                // SUCCESS: Log metadata for traceability
                log.info("Sent message=[{}] with offset=[{}] to partition=[{}]",event.getTopicId(),result.getRecordMetadata().offset(),
                        result.getRecordMetadata().partition());
            } else {
                // FAILURE: Log error for alerting/monitoring
                log.error("Unable to send message=[{}] due to : {}",event.getTopicId(), ex.getMessage());
            }
        });
    }
}