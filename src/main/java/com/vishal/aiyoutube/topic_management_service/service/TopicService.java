package com.vishal.aiyoutube.topic_management_service.service;

import com.vishal.aiyoutube.topic_management_service.dto.AnalysisCompletedEvent;
import com.vishal.aiyoutube.topic_management_service.dto.TopicRequest;
import com.vishal.aiyoutube.topic_management_service.dto.TopicResponse;

import java.util.UUID;

/**
 * Core Service interface defining the business logic for Research Topic management.
 * Orchestrates the transition between user requests, Kafka events, and persistent storage.
 */
public interface TopicService {

    /**
     * The primary entry point for a new user research request.
     * * EXECUTION FLOW:
     * 1. Performs SEO normalization on the raw query via Grok AI agents.
     * 2. Checks PostgreSQL for existing reports to prevent duplicate AI costs.
     * 3. Persists the TopicEntity with an initial status of PENDING.
     * 4. Dispatches a TopicSubmittedEvent to Kafka to trigger downstream microservices.
     *
     * @param request The DTO containing the user's raw query string.
     * @return A TopicResponse containing the assigned UUID and the initial PENDING status.
     */
    TopicResponse createTopicRequest(TopicRequest request);

    /**
     * Retrieves the current state, progress messages, and synthesized insights for a topic.
     * * USAGE:
     * Facilitates the frontend polling mechanism. It maps the current database state
     * (including partial video highlights) into a format suitable for the dashboard.
     *
     * @param topicId The unique UUID of the research topic.
     * @return A comprehensive TopicResponse including status and any available AI results.
     */
    TopicResponse getTopicDetails(UUID topicId);

    /**
     * Finalizes the research topic once the Llama-3 synthesis agents have finished.
     * * EXECUTION FLOW:
     * 1. Transitions the Topic status to COMPLETED.
     * 2. Maps incoming VideoSegmentDTOs to persistent VideoInsightEntities.
     * 3. Stores the final Executive Summary, Sentiment Score, and Consensus metrics.
     * 4. Triggers a database commit to make the results visible to the user.
     *
     * @param event The synthesized intelligence payload received from Kafka.
     */
    void updateTopicWithAnalysis(AnalysisCompletedEvent event);

    /**
     * Gracefully handles errors encountered within the asynchronous pipeline.
     * * USAGE:
     * Invoked if the YouTube scraper or AI Analysis service encounters an
     * unrecoverable error (e.g., API Rate Limits or Invalid Content).
     *
     * @param topicId The unique identifier of the failing topic.
     * @param reason A descriptive message explaining the cause of the failure.
     */
    void handleTopicFailure(UUID topicId, String reason);
}