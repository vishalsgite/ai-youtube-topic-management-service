package com.vishal.aiyoutube.topic_management_service.controller;

import com.vishal.aiyoutube.topic_management_service.dto.TopicRequest;
import com.vishal.aiyoutube.topic_management_service.dto.TopicResponse;
import com.vishal.aiyoutube.topic_management_service.service.TopicService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST Controller for managing Topic Analysis requests.
 * Provides endpoints for initiating the asynchronous microservices pipeline
 * and retrieving the synthesized intelligence reports.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/topics")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Enables frontend access from any origin (e.g., Live Server or React)
public class TopicController {

    private final TopicService topicService;

    /**
     * POST /api/v1/topics
     * Entry point for a user to submit a topic for AI analysis.
     * * FLOW:
     * 1. Receives raw user query.
     * 2. Calls Service to perform SEO normalization and Deduplication.
     * 3. Triggers the asynchronous Kafka pipeline.
     * * @param request The DTO containing the user's query (e.g., "AI job trends").
     * @return 202 Accepted, indicating the long-running task has started successfully.
     */
    @PostMapping
    public ResponseEntity<TopicResponse> analyzeTopic(@RequestBody TopicRequest request) {
        log.info("Received request to analyze topic: {}", request.getQuery());

        TopicResponse response = topicService.createTopicRequest(request);

        // We return 202 Accepted because the analysis is an ongoing background task
        // that involves multiple microservices and AI agents.
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    /**
     * GET /api/v1/topics/{id}
     * Polling endpoint used by the frontend to check the current status and fetch results.
     * * USAGE:
     * The index.html client calls this every 2 seconds to refresh the Source Highlights
     * and finally the Executive Summary once the status moves to COMPLETED.
     * * @param id The unique UUID assigned to the topic request.
     * @return The current state of the topic including any available AI insights.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TopicResponse> getTopicStatus(@PathVariable UUID id) {
        log.info("Fetching status/results for topic ID: {}", id);

        TopicResponse response = topicService.getTopicDetails(id);

        return ResponseEntity.ok(response);
    }
}