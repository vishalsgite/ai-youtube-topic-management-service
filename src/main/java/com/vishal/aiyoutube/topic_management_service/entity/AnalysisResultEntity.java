package com.vishal.aiyoutube.topic_management_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A JPA Embeddable component that stores the final AI-generated synthesis results.
 * These fields are mapped directly into the 'topics' table in the database.
 */
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisResultEntity {

    /**
     * The cohesive executive summary generated after analyzing all video sources.
     * Defined as TEXT to handle long-form AI reports without truncation.
     */
    @Column(columnDefinition = "TEXT")
    private String finalSummary;

    /**
     * A normalized value (0.0 to 1.0) representing the overall public perception.
     * Captured from the AI synthesis to populate the 'Sentiment' dashboard metric.
     */
    private Double sentimentScore;

    /**
     * The factual agreement percentage (0 to 100) calculated by the AI agents.
     * Represents the reliability of information across multiple video sources.
     */
    private Double consensusPercentage;

    /**
     * A detailed list or paragraph of factual points identified as 'Common Claims'.
     * Defined as TEXT to ensure large lists of claims are stored safely.
     */
    @Column(columnDefinition = "TEXT")
    private String commonClaims;
}
