package com.vishal.aiyoutube.topic_management_service.dto;

import lombok.Builder;
import lombok.Data;

/**
 * Data Transfer Object (DTO) representing an individual highlight or insight
 * extracted from a specific YouTube video.
 * This is used to populate the source intelligence cards on the frontend dashboard.
 */
@Data
@Builder
public class VideoInsightResponse {

    /**
     * The title of the YouTube video.
     * Displayed as small metadata (e.g., text-[10px]) to provide source context.
     */
    private String videoTitle;

    /**
     * The direct URL to the source video.
     * Combined with the timestamp on the frontend to create functional 'Jump to' links.
     */
    private String videoUrl;

    /**
     * The specific time position in the video where the insight occurs.
     * Format: "MM:SS" (e.g., "04:20").
     */
    private String timestamp;

    /**
     * A detailed AI-generated explanation providing deep context
     * on why this specific segment is relevant to the user's query.
     */
    private String explanation;

    /**
     * A concise, high-impact headline for the insight.
     * Displayed in a large font (e.g., text-xl) as the primary card title.
     */
    private String summary;
}
