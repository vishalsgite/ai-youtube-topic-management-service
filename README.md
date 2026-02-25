# 🚀 Topic Management Service

[![Java](https://img.shields.io/badge/Java-17%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Kafka](https://img.shields.io/badge/Apache-Kafka-231F20?style=for-the-badge&logo=apachekafka&logoColor=white)](https://kafka.apache.org/)
[![Docker](https://img.shields.io/badge/Docker-Enabled-2496ED?style=for-the-badge&logo=docker&logoColor=white)](https://www.docker.com/)

## 📌 Project Overview
The **Topic Management Service** is a high-performance microservice designed for the orchestration and dynamic configuration of messaging channels within a FinTech ecosystem. It serves as the "Control Plane" for your **NPCI Switch Clone**, managing how transaction data is parsed, routed, and audited across various payment rails.

### Key Capabilities:
* **Dynamic Kafka Orchestration:** Programmatic creation and monitoring of Kafka topics.
* **Grok-Based Log Parsing:** Real-time extraction of structured payment data from raw logs using the Grok engine.
* **Centralized Metadata Store:** Persistent tracking of topic configurations, partitions, and replication factors.
* **Scalable Architecture:** Built for high-throughput environments with stateless processing.

---

## 🏗 System Architecture
This service acts as the bridge between raw event streams and structured data storage.

1.  **Ingestion Layer:** Raw payment events (ISO 8583, JSON, or Delimited) hit **Apache Kafka**.
2.  **Processing Layer:** The service applies **Grok Patterns** to "un-structure" the data into meaningful fields (e.g., `transaction_id`, `mcc`, `rrn`).
3.  **Storage Layer:** Parsed metadata and configuration states are persisted in the **RDBMS (SQL)** for audit and reporting.

---

## 🛠 Tech Stack
* **Framework:** Spring Boot 3.x (Java 17)
* **Messaging:** Apache Kafka (Spring Kafka)
* **Parsing Engine:** Logstash-Grok / Java-Grok
* **Database:** PostgreSQL / MySQL (JPA/Hibernate)
* **Containerization:** Docker & Docker Compose

---

## 🚀 Getting Started

### Prerequisites
* **JDK 17** or higher
* **Maven 3.8+**
* **Docker Desktop** (with Compose)

### Installation & Run
1.  **Clone the Repository:**
    ```bash
    git clone [https://github.com/vishalsgite/ai-youtube-topic-management-service.git)
    cd topic-management-service
    ```

2.  **Environment Setup:**
    Ensure your `application.yml` points to your Kafka broker:
    ```yaml
    spring:
      kafka:
        bootstrap-servers: localhost:9092
      datasource:
        url: jdbc:postgresql://localhost:5432/topic_db
    ```

3.  **Build and Run:**
    ```bash
    mvn clean install
    java -jar target/topic-management-service.jar
    ```

---

## 🐳 Docker Configuration
To spin up the entire ecosystem (App + Kafka + Zookeeper + DB) in one go:

```bash
# Start the infrastructure
docker-compose up -d

# Check logs
docker logs -f topic-management-service
