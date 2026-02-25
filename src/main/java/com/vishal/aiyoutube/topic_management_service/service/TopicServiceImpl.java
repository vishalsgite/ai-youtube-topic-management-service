package com.vishal.aiyoutube.topic_management_service.service;

import com.vishal.aiyoutube.topic_management_service.config.GrokClient;
import com.vishal.aiyoutube.topic_management_service.dto.*;
import com.vishal.aiyoutube.topic_management_service.entity.*;
import com.vishal.aiyoutube.topic_management_service.exceptions.AnalysisProcessingException;
import com.vishal.aiyoutube.topic_management_service.kafka.producer.TopicEventProducer;
import com.vishal.aiyoutube.topic_management_service.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of the TopicService responsible for the end-to-end lifecycle of research topics.
 * Manages SEO normalization via Grok AI, persistent deduplication, and Kafka event orchestration.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TopicServiceImpl implements TopicService {

    private final TopicRepository topicRepository;
    private final TopicEventProducer eventProducer;
    private final GrokClient grokClient;

    @Override
    @Transactional
    public TopicResponse createTopicRequest(TopicRequest request) {
        log.info("Processing original user request: {}", request.getQuery());

        String aiResponse = grokClient.chat(
                "You are a YouTube Search SEO expert. " +
                        "Convert the user's request into a single search string of 5 to 6 keywords. " +
                        "Rules: Return ONLY keywords, no quotes, no backticks, no lists.",
                request.getQuery()
        );

        String cleanedResponse = aiResponse.split("\\n")[0]
                .replaceAll("[\\\"'`]", "")
                .replaceAll("[^a-zA-Z0-9\\s]", "")
                .replaceAll("\\s+", " ")
                .trim();

        String[] words = cleanedResponse.split(" ");
        String unifiedQuery = words.length > 6 ?
                String.join(" ", java.util.Arrays.copyOfRange(words, 0, 6)) : cleanedResponse;

        log.info("Sanitized query for YouTube: {}", unifiedQuery);

        Optional<TopicEntity> existingTopic = topicRepository.findByNormalizedQuery(unifiedQuery);
        if (existingTopic.isPresent()) {
            log.info("Deduplication: Found existing record for: {}", unifiedQuery);
            return mapToResponse(existingTopic.get());
        }

        TopicEntity entity = TopicEntity.builder()
                .rawQuery(request.getQuery())
                .normalizedQuery(unifiedQuery)
                .status(TopicStatusEntity.PENDING)
                .videoInsights(new ArrayList<>())
                .build();

        entity = topicRepository.save(entity);
        eventProducer.sendTopicSubmittedEvent(new TopicSubmittedEvent(entity.getId(), unifiedQuery));

        return mapToResponse(entity);
    }

    @Override
    public TopicResponse getTopicDetails(UUID topicId) {
        TopicEntity entity = topicRepository.findById(topicId)
                .orElseThrow(() -> new AnalysisProcessingException("Topic not found: " + topicId, null));
        return mapToResponse(entity);
    }

    @Override
    @Transactional
    public void handleTopicFailure(UUID topicId, String reason) {
        topicRepository.findById(topicId).ifPresent(entity -> {
            entity.setStatus(TopicStatusEntity.FAILED);
            topicRepository.save(entity);
            log.error("Pipeline failure for {}: {}", topicId, reason);
        });
    }

    @Override
    @Transactional
    public void updateTopicWithAnalysis(AnalysisCompletedEvent event) {
        TopicEntity entity = topicRepository.findById(event.getTopicId())
                .orElseThrow(() -> new AnalysisProcessingException("Sync error: Topic ID not found", null));

        boolean isFinal = event.getFinalSummary() != null &&
                !event.getFinalSummary().contains("Analyzing video");

        if (isFinal) {
            entity.setStatus(TopicStatusEntity.COMPLETED);
            entity.setAnalysisResult(new AnalysisResultEntity(
                    event.getFinalSummary(),
                    event.getSentimentScore(),
                    event.getConsensusPercentage(),
                    event.getCommonClaims()
            ));
        }

        if (event.getSegments() != null) {
            if (entity.getVideoInsights() == null) entity.setVideoInsights(new ArrayList<>());

            for (var segDTO : event.getSegments()) {
                boolean alreadyExists = entity.getVideoInsights().stream()
                        .anyMatch(existing -> existing.getVideoId().equals(segDTO.getVideoId()) &&
                                existing.getTimestamp().equals(segDTO.getTimestamp()));

                if (!alreadyExists) {
                    entity.getVideoInsights().add(VideoInsightEntity.builder()
                            .topic(entity)
                            .videoId(segDTO.getVideoId())
                            .videoTitle(segDTO.getVideoTitle())
                            .videoUrl(segDTO.getVideoUrl())
                            .timestamp(segDTO.getTimestamp())
                            .bestExplanation(segDTO.getBestExplanation())
                            .segmentSummary(segDTO.getSegmentSummary())
                            .build());
                }
            }
        }
        topicRepository.save(entity);
    }

    /**
     * Maps the persistent database Entity to a Response DTO for API consumption.
     * UPDATED: Added rigorous null-safety for AnalysisResult and numeric fields.
     */
    private TopicResponse mapToResponse(TopicEntity entity) {
        // 1. Rigorous check for the AnalysisResult object
        AnalysisResultEntity result = entity.getAnalysisResult();
        boolean hasResult = (result != null);

        return TopicResponse.builder()
                .topicId(entity.getId())
                .query(entity.getNormalizedQuery() != null ? entity.getNormalizedQuery() : entity.getRawQuery())
                .status(entity.getStatus())

                // 2. Safe Summary Mapping
                .summary(hasResult && result.getFinalSummary() != null
                        ? result.getFinalSummary() : "Analysis in progress...")

                // 3. SAFE NUMERIC MAPPING: Prevents unboxing NullPointerException
                .sentimentScore(hasResult && result.getSentimentScore() != null
                        ? result.getSentimentScore() : 0.0)

                .consensusPercentage(hasResult && result.getConsensusPercentage() != null
                        ? result.getConsensusPercentage() : 0.0)

                .commonClaims(hasResult && result.getCommonClaims() != null
                        ? result.getCommonClaims() : "Gathering claims...")

                // 4. Safe List Mapping
                .videoHighlights(entity.getVideoInsights() == null ? new ArrayList<>() :
                        entity.getVideoInsights().stream()
                                .map(i -> VideoInsightResponse.builder()
                                        .videoTitle(i.getVideoTitle())
                                        .videoUrl(i.getVideoUrl())
                                        .timestamp(i.getTimestamp())
                                        .explanation(i.getBestExplanation())
                                        .summary(i.getSegmentSummary())
                                        .build())
                                .collect(Collectors.toList()))
                .build();
    }
}