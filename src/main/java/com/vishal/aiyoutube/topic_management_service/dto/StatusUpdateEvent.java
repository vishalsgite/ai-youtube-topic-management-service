package com.vishal.aiyoutube.topic_management_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) used to communicate real-time pipeline status updates.
 * This event is published by various microservices to keep the Topic Management Service
 * and the end-user informed about the current progress of the analysis.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatusUpdateEvent {

    /**
     * The unique identifier for the topic being processed.
     * Maps the status update to a specific record in the PostgreSQL database.
     */
    private UUID topicId;

    /**
     * The current state of the processing pipeline.
     * Valid values typically include:
     * - EXTRACTING: Scraping transcripts from YouTube.
     * - ANALYZING: Running Llama-3 AI processing on specific video chunks.
     * - COMPLETED: All agents have finished synthesis.
     * - FAILED: Pipeline stopped due to errors (e.g., API Rate Limits).
     */
    private String status;

    /**
     * A human-readable detail message explaining the specific sub-task.
     * Examples:
     * - "Analyzing video 1 of 3..."
     * - "Synthesizing global consensus..."
     * - "AI Error: Rate limit reached."
     */
    private String message;
}