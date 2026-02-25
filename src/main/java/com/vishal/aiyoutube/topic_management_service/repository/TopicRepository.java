package com.vishal.aiyoutube.topic_management_service.repository;

import com.vishal.aiyoutube.topic_management_service.entity.TopicEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for managing persistent {@link TopicEntity} instances.
 * Extends JpaRepository to provide standard CRUD operations and custom query capabilities
 * for the 'topics' table in the PostgreSQL database.
 */
@Repository
public interface TopicRepository extends JpaRepository<TopicEntity, UUID> {

    /**
     * Finds an existing topic based on the AI-normalized search keywords.
     * This method is critical for the system's deduplication logic.
     * * USAGE:
     * When a new request arrives, the service layer calls this method using the
     * Grok-normalized query. If a result is found, the system returns the existing
     * report instead of triggering a new, expensive AI analysis pipeline.
     *
     * @param normalizedQuery The unified, SEO-optimized search term (e.g., "India Union Budget 2026").
     * @return An Optional containing the topic if it was previously processed and stored.
     */
    Optional<TopicEntity> findByNormalizedQuery(String normalizedQuery);
}