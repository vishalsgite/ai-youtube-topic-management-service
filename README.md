# 🛰️ Nexus AI: Topic Management Service (Service 1)

The **Topic Management Service** acts as the primary API Gateway and Orchestrator for the Nexus AI ecosystem. It is responsible for ingestion, state management, and final data persistence for the entire multi-agent research pipeline.

---

## 🛠️ Service Overview

This service provides the interface between the end-user and the asynchronous backend. It leverages an event-driven pattern to ensure that the user experience remains fast and responsive, even while heavy AI processing occurs in the background.

### Core Responsibilities:
* **Request Ingestion:** Receives raw research queries via REST endpoints.
* **Event Production:** Publishes `TopicSubmittedEvent` to Kafka to trigger the research agents.
* **State Tracking:** Manages the lifecycle of a request (PENDING ➔ EXTRACTING ➔ ANALYZING ➔ COMPLETED).
* **Result Aggregation:** Consumes final synthesis data and persists it for UI retrieval.

---

## 🏗️ Architecture Design

The service implements a **Bi-Directional Messaging Pattern** using Apache Kafka to maintain a decoupled relationship with downstream processing agents.


### Tech Stack:
* **Framework:** Spring Boot 3.3.x
* **Database:** PostgreSQL (Primary State Store)
* **Messaging:** Apache Kafka (Event Bus)
* **AI Integration:** Groq Cloud (Llama 3.x) for query normalization.

---

## 📡 API Reference

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/api/topics/analyze` | Ingests query, saves initial record, and triggers Kafka event. |
| `GET` | `/api/topics/{id}` | Returns real-time status and final research insights. |
| `GET` | `/index.html` | Serves the frontend research dashboard. |

---

## 🚀 Deployment & Docker Steps

### 1. Environment Configuration
Create a `.env` file or set the following properties:
```properties
SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:9092
SPRING_DATASOURCE_URL=jdbc:postgresql://youtube-insight-postgres:5432/nexus_db
GROQ_API_KEY=your_api_key_here
