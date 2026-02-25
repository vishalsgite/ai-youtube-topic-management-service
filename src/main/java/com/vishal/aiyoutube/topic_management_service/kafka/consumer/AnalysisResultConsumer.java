package com.vishal.aiyoutube.topic_management_service.kafka.consumer;


import com.vishal.aiyoutube.topic_management_service.dto.AnalysisCompletedEvent;
import com.vishal.aiyoutube.topic_management_service.exceptions.AnalysisProcessingException;
import com.vishal.aiyoutube.topic_management_service.service.TopicService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Kafka Consumer responsible for processing the final intelligence output from the AI Analysis Service.
 * This class acts as the bridge between the asynchronous AI processing pipeline
 * and the persistent database storage in the Topic Management Service.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AnalysisResultConsumer {

    /**
     * Service layer dependency used to persist the synthesized AI data
     * and update the topic state in PostgreSQL.
     */
    private final TopicService topicService;

    /**
     * Listens to the 'analysis-completed-events' topic.
     * * OPERATION:
     * 1. Triggered automatically when the AI service publishes a final or partial synthesis.
     * 2. Extracts the Topic ID, Executive Summary, Metrics, and Video Highlights from the event.
     * 3. Delegates the heavy lifting of database synchronization to the TopicService.
     *
     * @param event The DTO containing the finalized intelligence report and source highlights.
     */
    @KafkaListener(
            topics = "analysis-completed-events",
            groupId = "topic-service-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeAnalysisResult(AnalysisCompletedEvent event) {
        log.info("Received AnalysisCompletedEvent from Kafka for Topic ID: {}", event.getTopicId());

        try {
            /**
             * PERSISTENCE LOGIC:
             * Invokes the service to update the TopicEntity status to COMPLETED,
             * store the AnalysisResultEntity (Summary/Consensus), and map the
             * VideoInsightEntity list (Highlights/Links).
             */
            topicService.updateTopicWithAnalysis(event);

            log.info("Successfully processed and persisted analysis for Topic ID: {}", event.getTopicId());

        } catch (Exception e) {
            /**
             * SPECIFIC EXCEPTION HANDLING:
             * Instead of a generic catch-all, we wrap the failure in a domain-specific
             * AnalysisProcessingException. This prevents the consumer from silently failing
             * and allows for better tracking of persistence-related issues.
             */
            log.error("CRITICAL: Persistence failure for Topic ID: {}. Error: {}",event.getTopicId(), e.getMessage());

            throw new AnalysisProcessingException(
                    "Failed to update database for topic: " + event.getTopicId(), e);
        }
    }
}