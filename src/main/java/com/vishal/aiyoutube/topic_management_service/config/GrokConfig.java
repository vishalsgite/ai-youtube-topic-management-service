package com.vishal.aiyoutube.topic_management_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;

import java.time.Duration;

/**
 * Configuration class responsible for initializing the WebClient bean
 * dedicated to Groq AI API interactions.
 * It manages timeout settings and base connection parameters.
 */
@Configuration
public class GrokConfig {

    /**
     * Configures and creates a WebClient bean.
     * * @param baseUrl The endpoint of the Groq API (e.g., https://api.groq.com).
     * @param timeoutSeconds The maximum duration to wait for an AI response before
     * terminating the request to prevent thread hanging.
     * @return A configured WebClient instance.
     */
    @Bean
    public WebClient grokWebClient(
            @Value("${grok.base-url}") String baseUrl,
            @Value("${grok.timeout-seconds}") int timeoutSeconds
    ) {

        /**
         * HTTP CLIENT CUSTOMIZATION:
         * We create a specialized Netty HttpClient to enforce response timeouts.
         * This is critical because LLM APIs can occasionally have high latency,
         * and we don't want our microservice resources locked indefinitely.
         */
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(timeoutSeconds));

        /**
         * WEBCLIENT BUILDER:
         * Uses the ReactorClientHttpConnector to link the Netty HttpClient settings
         * with the Spring WebClient abstraction.
         */
        return WebClient.builder()
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}