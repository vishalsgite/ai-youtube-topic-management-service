package com.vishal.aiyoutube.topic_management_service.dto;

import lombok.Data;

/**
 * Data Transfer Object (DTO) representing the initial user request.
 * This is the payload received from the REST controller when a user
 * submits a topic for AI synthesis.
 */
@Data
public class TopicRequest {

    /**
     * The raw search query entered by the user.
     * Examples:
     * - "Future of AI Jobs"
     * - "Maharashtra Political Crisis Summary"
     * - "Best skincare routine for men"
     * * This query is later sent to the Grok SEO Normalizer to be
     * converted into optimized YouTube search keywords.
     */
    private String query;
}
