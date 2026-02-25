package com.vishal.aiyoutube.topic_management_service.kafka.consumer;

import com.vishal.aiyoutube.topic_management_service.dto.StatusUpdateEvent;
import com.vishal.aiyoutube.topic_management_service.entity.TopicStatusEntity;
import com.vishal.aiyoutube.topic_management_service.exceptions.StatusSyncException;
import com.vishal.aiyoutube.topic_management_service.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Kafka Consumer responsible for tracking the real-time progress of the analysis pipeline.
 * It listens for status signals from downstream services (YouTube and AI) and
 * synchronizes the primary 'topics' table state.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StatusUpdateConsumer {

    /**
     * Repository used to perform lookups and updates on the 'topics' table.
     */
    private final TopicRepository topicRepository;

    /**
     * Consumes status updates from both YouTube Service (Service 2) and AI Service (Service 3).
     * * OPERATION:
     * 1. Extracts the incoming status string and Topic UUID.
     * 2. Maps the string to the internal TopicStatusEntity enum.
     * 3. Performs an atomic database update to reflect the current pipeline stage.
     *
     * @param event The DTO containing the topic ID, status string, and optional detail message.
     */
    @Transactional
    @KafkaListener(
            topics = "topic-status-updates",
            groupId = "topic-service-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeStatusUpdate(StatusUpdateEvent event) {
        log.info("Kafka Consumer: Received status [{}] for Topic ID [{}]",
                event.getStatus(), event.getTopicId());

        // Basic validation to prevent processing of corrupted messages
        if (event.getTopicId() == null) {
            log.error("Discarding event: Received StatusUpdateEvent with null Topic ID");
            return;
        }

        // Locate the record in PostgreSQL before attempting an update
        topicRepository.findById(event.getTopicId()).ifPresentOrElse(topic -> {
            try {
                /**
                 * ENUM MAPPING:
                 * Converts the incoming string (e.g., "extracting") into the
                 * typed Enum (TopicStatusEntity.EXTRACTING).
                 */
                TopicStatusEntity newStatus = TopicStatusEntity.valueOf(event.getStatus().toUpperCase());

                topic.setStatus(newStatus);

                /**
                 * TRANSACTIONAL COMMIT:
                 * saveAndFlush ensures the persistence provider pushes the change to the
                 * database immediately. This is vital for the frontend polling mechanism
                 * to see the change instantly.
                 */
                topicRepository.saveAndFlush(topic);

                log.info("Database Updated: Topic {} is now {}", event.getTopicId(), newStatus);

            } catch (IllegalArgumentException e) {
                /**
                 * DOMAIN VALIDATION ERROR:
                 * Occurs if a downstream service sends a status string that isn't
                 * defined in our local enum (PENDING, EXTRACTING, etc.).
                 */
                log.error("Validation Failure: Status '{}' does not match TopicStatusEntity enums.",
                        event.getStatus());
            } catch (Exception e) {
                /**
                 * PERSISTENCE FAILURE:
                 * Wraps unexpected runtime or SQL errors in a StatusSyncException
                 * to ensure the failure is explicitly tracked in logs.
                 */
                throw new StatusSyncException(
                        "Unexpected error updating status for Topic ID " + event.getTopicId(), e);
            }
        }, () -> log.warn("Database Sync Skip: Topic ID {} not found in topics table", event.getTopicId()));
    }
}