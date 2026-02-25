package com.vishal.aiyoutube.topic_management_service.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

/**
 * Data Transfer Object (DTO) used to construct requests for the Groq/Grok AI API.
 * This class follows the standard Chat Completion structure required by LLM providers.
 */
@Data
@Builder
public class GrokChatRequest {

    /**
     * The specific AI model ID to be used for the request.
     * Examples: "llama-3.1-8b-instant", "llama-3.3-70b-versatile".
     */
    private String model;

    /**
     * A list of messages comprising the conversation history.
     * Includes system instructions and user queries to provide context to the model.
     */
    private List<Message> messages;

    /**
     * Controls the randomness of the model's output.
     * Values typically range from 0.0 (deterministic/focused) to 1.0 (creative/random).
     */
    private Double temperature;

    /**
     * Inner class representing a single message within the chat conversation.
     */
    @Data
    @Builder
    public static class Message {

        /**
         * The role of the message author.
         * Typical values:
         * - "system": For background instructions and constraints.
         * - "user": For the actual prompt or question from the end-user.
         * - "assistant": For previous responses from the AI.
         */
        private String role;

        /**
         * The actual text content of the message.
         * This can be the system prompt or the raw user query.
         */
        private String content;
    }
}