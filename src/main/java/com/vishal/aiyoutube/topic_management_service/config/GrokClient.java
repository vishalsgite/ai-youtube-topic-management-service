package com.vishal.aiyoutube.topic_management_service.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vishal.aiyoutube.topic_management_service.dto.GrokChatResponse;
import com.vishal.aiyoutube.topic_management_service.exceptions.AnalysisProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Client component for interacting with the Groq Cloud API (utilizing Llama-3 models).
 * This class handles the construction of chat completion requests and extracts
 * AI-generated content for SEO normalization and search optimization.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GrokClient {

    /**
     * Non-blocking, reactive web client configured for the Groq base URL.
     */
    private final WebClient grokWebClient;

    @Value("${grok.api-key}")
    private String apiKey;

    @Value("${grok.model}")
    private String model;

    @Value("${grok.temperature}")
    private Double temperature;

    /**
     * Local Jackson ObjectMapper configured with a 'lenient' strategy.
     * It ensures the application doesn't crash if Groq adds new fields to their API response.
     */
    private final ObjectMapper lenientMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    /**
     * Sends a synchronous chat completion request to the AI model.
     * * @param systemPrompt The background instructions (e.g., "You are an SEO expert").
     * @param userPrompt   The raw user input query.
     * @return The AI's text response (normalized keywords).
     * @throws AnalysisProcessingException if the API call or parsing fails.
     */
    public String chat(String systemPrompt, String userPrompt) {
        // 1. Prepare Request Body: Constructing the standard Chat Completion JSON structure
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        requestBody.put("temperature", temperature);

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", systemPrompt));
        messages.add(Map.of("role", "user", "content", userPrompt));
        requestBody.put("messages", messages);

        log.debug("Sending query to Groq Cloud using model: {}", model);

        // 2. Execute Request: Using WebClient to post the payload and handle status errors
        String rawResponse = grokWebClient.post()
                .uri("/openai/v1/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(status -> status.isError(), response ->
                        response.bodyToMono(String.class)
                                .map(body -> {
                                    log.error("Groq API Error: {}", body);
                                    return new AnalysisProcessingException("Groq API communication failed: " + body, null);
                                })
                )
                .bodyToMono(String.class)
                .block(); // Synchronous block to wait for the normalization before proceeding

        // 3. Parse and Log Usage: Extracting the content and monitoring token consumption
        try {
            GrokChatResponse response = lenientMapper.readValue(rawResponse, GrokChatResponse.class);

            if (response.getChoices() == null || response.getChoices().isEmpty()) {
                throw new AnalysisProcessingException("Groq returned empty choices in response", null);
            }

            /**
             * TOKEN MONITORING:
             * Essential for tracking Free Tier quotas (e.g., 100k tokens/day).
             * Logs the prompt and completion tokens used for each SEO normalization.
             */
            if (response.getUsage() != null) {
                log.info("Groq Token Usage -> Prompt: {}, Completion: {}, Total: {}",
                        response.getUsage().getPrompt_tokens(),
                        response.getUsage().getCompletion_tokens(),
                        response.getUsage().getTotal_tokens());
            }

            return response.getChoices().get(0).getMessage().getContent();

        } catch (Exception e) {
            log.error("Failed to parse Groq response. Raw body: {}", rawResponse);
            throw new AnalysisProcessingException("AI Response Parsing Failed", e);
        }
    }
}