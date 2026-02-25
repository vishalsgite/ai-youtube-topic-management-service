package com.vishal.aiyoutube.topic_management_service.entity;


import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "video_insights")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VideoInsightEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id")
    private TopicEntity topic;

    private String videoId;
    private String videoTitle;
    private String videoUrl;

    /**
     * Why this video is good for this specific point
     */
    @Column(columnDefinition = "TEXT")
    private String bestExplanation;

    /**
     * e.g., "05:20" or "320" (seconds)
     */
    private String timestamp;

    /**
     * Short summary of what happens at that timestamp
     */
    @Column(columnDefinition = "TEXT")
    private String segmentSummary;
}