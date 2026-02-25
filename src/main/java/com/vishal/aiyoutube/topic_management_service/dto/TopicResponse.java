package com.vishal.aiyoutube.topic_management_service.dto;

import com.vishal.aiyoutube.topic_management_service.entity.TopicStatusEntity;
import lombok.Builder;
import lombok.Data;
import java.util.List;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) representing the comprehensive response for a topic.
 * This is the primary object polled by the frontend to display the final
 * analysis dashboard and real-time status updates.
 */
@Data
@Builder
public class TopicResponse {

    /**
     * Unique identifier for the processed topic.
     * Used by the frontend to track specific requests.
     */
    private UUID topicId;

    /**
     * The normalized SEO search query used to find videos.
     * Derived from the raw user input via Grok SEO normalization.
     */
    private String query;

    /**
     * The current lifecycle state of the request.
     * Values: PENDING, EXTRACTING, ANALYZING, COMPLETED, FAILED.
     */
    private TopicStatusEntity status;

    /**
     * AI-generated high-level report.
     * Provides a 2-3 sentence executive summary synthesized from all video sources.
     */
    private String summary;

    /**
     * Overall perception score (0.0 to 1.0).
     * Maps to the 'Public Sentiment' progress bar on the UI.
     */
    private Double sentimentScore;

    /**
     * Factual agreement percentage (0 to 100).
     * Maps to the 'Consensus Score' display on the UI.
     */
    private Double consensusPercentage;

    /**
     * Stringified list of factual claims found across multiple video sources.
     */
    private String commonClaims;

    /**
     * A collection of specific highlight moments from individual videos.
     * These populate the 'Intelligence Sources' grid at the bottom of the dashboard.
     */
    private List<VideoInsightResponse> videoHighlights;
}