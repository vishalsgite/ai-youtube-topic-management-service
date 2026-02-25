package com.vishal.aiyoutube.topic_management_service.entity;

import lombok.Getter;
import lombok.ToString;

/**
 * Enumeration representing the lifecycle states of a research topic.
 * This enum acts as a state machine to track the progress of a request
 * across the microservices ecosystem.
 */
@Getter
@ToString
public enum TopicStatusEntity {

    /**
     * Initial state: The request has been received by the Topic Management API
     * and SEO normalization is complete, but downstream processing hasn't started.
     */
    PENDING,

    /**
     * Data acquisition state: The YouTube Service is currently searching for
     * relevant videos and scraping their transcripts/metadata.
     */
    EXTRACTING,

    /**
     * Intelligence processing state: The AI Analysis Service is actively
     * running Llama-3 agents to synthesize video insights and global consensus.
     */
    ANALYZING,

    /**
     * Success state: The multi-agent pipeline has finished, and the final
     * executive report and highlights are stored in the database.
     */
    COMPLETED,

    /**
     * Error state: An issue occurred in any service within the chain (e.g.,
     * YouTube API quota exceeded, Groq rate limits, or network failure).
     */
    FAILED
}