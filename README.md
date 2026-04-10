<div align="center">
  
[![Typing SVG](https://readme-typing-svg.demolab.com?font=JetBrains+Mono&weight=700&size=24&pause=1000&color=00D4FF&center=true&vCenter=true&width=750&lines=🚀+SmartEvent+Booking+Platform;Production-Grade+Microservices+%7C+Java+21;Event-Driven+%7C+Secure+%7C+Cloud-Native;Spring+Boot+3+%7C+Kafka+%7C+JWT+%7C+Docker)](https://git.io/typing-svg)

<br>

# 🚀 SmartEvent — Event Booking Microservices Platform

### `Java 21` · `Spring Boot 3` · `Kafka` · `JWT` · `Docker` · `Cloud-Native`

**A production-grade distributed backend platform** for managing events, bookings, payments, and notifications — built with **Java 21**, **Spring Boot 3**, and modern **Cloud-Native** principles.

<br/>

![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![Kafka](https://img.shields.io/badge/Apache%20Kafka-231F20?style=for-the-badge&logo=apachekafka&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white)
![Resilience4j](https://img.shields.io/badge/Resilience4j-Circuit%20Breaker-critical?style=for-the-badge)
![Zipkin](https://img.shields.io/badge/Zipkin-Tracing-FE7139?style=for-the-badge)
![Status](https://img.shields.io/badge/Status-Production%20Ready-00C851?style=for-the-badge)

</div>

---

## 📋 Table of Contents

- [Overview](#-overview)
- [System Architecture](#️-system-architecture)
- [Services Breakdown](#-services-breakdown)
- [Security Model](#-security-model)
- [Event-Driven Flow](#-event-driven-flow)
- [Tech Stack](#️-tech-stack)
- [Design Highlights](#-design-highlights)
- [Observability](#-observability)
- [Getting Started](#-getting-started)
- [Future Enhancements](#-future-enhancements)

---

## 🔍 Overview

SmartEvent is a **fully distributed, event-driven microservices platform** that demonstrates enterprise-level backend engineering:

| Capability | Implementation |
|---|---|
| 🔀 **API Routing & Security** | Spring Cloud Gateway |
| 🔍 **Service Discovery** | Netflix Eureka |
| ⚙️ **Centralized Config** | Spring Cloud Config Server |
| 🔐 **Authentication** | JWT + OAuth2 (Google Login) |
| 📨 **Async Messaging** | Apache Kafka |
| 🔗 **Inter-service Calls** | OpenFeign + Load Balancer |
| 🛡️ **Fault Tolerance** | Resilience4j Circuit Breaker |
| 📊 **Distributed Tracing** | Zipkin + Micrometer |
| 🗄️ **Data Isolation** | Database-per-Service (PostgreSQL) |
| 🐳 **Containerization** | Docker & Docker Compose |

---

## 🏛️ System Architecture

<p align="center">
  <img src="/Smartevent-Platform Image.png" width="100%" alt="SmartEvent System Architecture"/>
</p>

```
                        ┌──────────────────────┐
                        │      Client Apps      │
                        └──────────┬───────────┘
                                   │
                        ┌──────────▼───────────┐
                        │      API Gateway      │  ← JWT Validation, Routing
                        └──────────┬───────────┘
                                   │
               ┌───────────────────┼───────────────────┐
               │                   │                   │
    ┌──────────▼──────┐  ┌─────────▼──────┐  ┌────────▼────────┐
    │   User Service  │  │  Event Service  │  │ Booking Service │
    └──────────┬──────┘  └─────────┬──────┘  └────────┬────────┘
               │                   │                   │
               │          ┌────────▼────────┐          │
               │          │  Kafka Broker   │◄─────────┘
               │          └────────┬────────┘
               │                   │
    ┌──────────▼──────┐  ┌─────────▼──────┐
    │ Payment Service │  │  Notification   │
    └──────────┬──────┘  │    Service     │
               │          └────────────────┘
               ▼
    ┌─────────────────┐     ┌──────────────┐     ┌──────────────┐
    │  Config Server  │     │    Eureka    │     │    Zipkin    │
    │  (Port: 8888)   │     │ (Port: 8761) │     │ (Port: 9411) │
    └─────────────────┘     └──────────────┘     └──────────────┘
```

---

## 🧩 Services Breakdown

### 🔧 Infrastructure Services

| Service | Port | Description |
|---|---|---|
| **API Gateway** | `8080` | Single entry point — JWT validation, routing, rate limiting |
| **Eureka Server** | `8761` | Dynamic service registration & discovery |
| **Config Server** | `8888` | Centralized configuration for all services |
| **Zipkin** | `9411` | Distributed request tracing & latency monitoring |

---

### 💼 Business Services

| Service | Port | Responsibilities |
|---|---|---|
| **User Service** | `8081` | Registration, login, JWT generation, OAuth2 (Google), RBAC |
| **Event Service** | `8082` | Event CRUD, state machine, availability management |
| **Booking Service** | `8083` | Seat reservations, booking lifecycle management |
| **Payment Service** | `8085` | Payment processing, success/failure handling |
| **Notification Service** | `8084` | Async email/push notifications via Kafka consumers |

---

## 🔐 Security Model

### Authentication Strategy

- **JWT** tokens issued by User Service and validated at the **API Gateway**
- **OAuth2** integration with Google Login
- Tokens propagated via headers to all downstream services
- Fully **stateless** — no server-side sessions

### Role-Based Access Control (RBAC)

| Role | Permissions |
|---|---|
| `ADMIN` | Full system access — manage users, events, bookings |
| `ORGANIZER` | Create, update, cancel own events |
| `ATTENDEE` | Browse events, create & cancel bookings |

---

## 📨 Event-Driven Flow

All inter-service communication for async operations goes through **Apache Kafka**:

```
Booking Created
      │
      ▼
┌─────────────────┐     ┌──────────────────────┐
│ Booking Service │────►│   PaymentRequested   │ (Kafka Topic)
└─────────────────┘     └──────────┬───────────┘
                                   │
                        ┌──────────▼───────────┐
                        │   Payment Service    │
                        └──────────┬───────────┘
                                   │
               ┌───────────────────┴───────────────────┐
               │                                       │
    ┌──────────▼──────────┐               ┌────────────▼──────────┐
    │   PaymentSuccess    │               │     PaymentFailed      │
    └──────────┬──────────┘               └────────────┬──────────┘
               │                                       │
    ┌──────────▼──────────┐               ┌────────────▼──────────┐
    │  Booking Confirmed  │               │   Booking Cancelled   │
    │  + Notification     │               │   + Notification      │
    └─────────────────────┘               └───────────────────────┘

EventCancelled ──► Refund Triggered ──► Notification to all Attendees
```

---

## 🛠️ Tech Stack

<div align="center">

### ⚙️ Backend Core
![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)

### 🔐 Security
![Spring Security](https://img.shields.io/badge/Spring%20Security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-Stateless-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white)
![OAuth2](https://img.shields.io/badge/OAuth2-Google%20Login-4285F4?style=for-the-badge&logo=google&logoColor=white)

### 📨 Messaging & Communication
![Kafka](https://img.shields.io/badge/Apache%20Kafka-231F20?style=for-the-badge&logo=apachekafka&logoColor=white)
![OpenFeign](https://img.shields.io/badge/OpenFeign-REST-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![Resilience4j](https://img.shields.io/badge/Resilience4j-Circuit%20Breaker-FF6347?style=for-the-badge)

### 🗄️ Data
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)
![Hibernate](https://img.shields.io/badge/JPA%20%2F%20Hibernate-59666C?style=for-the-badge&logo=hibernate&logoColor=white)

### 📊 Observability
![Zipkin](https://img.shields.io/badge/Zipkin-Tracing-FE7139?style=for-the-badge)
![Micrometer](https://img.shields.io/badge/Micrometer-Metrics-1DB954?style=for-the-badge)
![Swagger](https://img.shields.io/badge/Swagger-OpenAPI-85EA2D?style=for-the-badge&logo=swagger&logoColor=black)

### 🐳 DevOps
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![Docker Compose](https://img.shields.io/badge/Docker%20Compose-2496ED?style=for-the-badge&logo=docker&logoColor=white)

</div>

---

## 🎯 Design Highlights

| Area | Implementation | Benefit |
|---|---|---|
| **Architecture** | Microservices | Independent deployment & scaling |
| **Data Management** | Database-per-Service | Data isolation & fault containment |
| **Communication** | Kafka Event-Driven | Loose coupling & async processing |
| **API Layer** | Spring Cloud Gateway | Centralized routing, security & rate limiting |
| **Service Discovery** | Netflix Eureka | Dynamic registration & lookup |
| **Configuration** | Spring Cloud Config | Centralized & environment-aware config |
| **Resilience** | Resilience4j Circuit Breaker | Fault tolerance & system stability |
| **Security** | JWT + OAuth2 | Stateless auth & social login |
| **Observability** | Zipkin + Micrometer | End-to-end tracing & performance metrics |
| **Deployment** | Docker & Docker Compose | Reproducible environments |

---

## 📊 Observability

| Tool | Purpose |
|---|---|
| **Zipkin** | Visualize distributed traces — latency, spans, dependencies |
| **Micrometer** | Collect JVM & application metrics |
| **Structured Logging** | Correlated logs across all services |
| **Swagger / OpenAPI** | Interactive API documentation per service |

**Access Dashboards:**

| Dashboard | URL |
|---|---|
| Eureka | http://localhost:8761 |
| Zipkin | http://localhost:9411 |
| API Gateway | http://localhost:8080 |

---

## 🚀 Getting Started

### ✅ Prerequisites

| Tool | Version |
|---|---|
| JDK | 21+ |
| Maven | 3.8+ |
| Docker | Latest |
| Docker Compose | Latest |

---

### 📥 Clone the Repository

```bash
git clone https://github.com/AhmedNawar2003/smartevent-platform.git
cd smartevent-platform
```

### 🔨 Build All Services

```bash
mvn clean package -DskipTests
```

### 🐳 Run with Docker Compose

```bash
docker-compose up --build
```

### 🔢 Manual Startup Order

| Step | Service | Why |
|---|---|---|
| 1 | **Config Server** | Must be up first — all services pull config from it |
| 2 | **Eureka Server** | Services need to register before accepting traffic |
| 3 | **Kafka Broker** | Required by Notification & Payment services |
| 4 | **Business Services** | User, Event, Booking, Payment, Notification |
| 5 | **API Gateway** | Last — routes to all registered services |

---

## 📈 Future Enhancements

- [ ] **Redis Caching** — Cache event listings and user sessions
- [ ] **Kubernetes Deployment** — Helm charts for production orchestration
- [ ] **CI/CD Pipeline** — GitHub Actions for automated build, test & deploy
- [ ] **Rate Limiting** — Per-user API throttling at the Gateway
- [ ] **Saga Pattern** — Distributed transaction management across services
- [ ] **React.js Frontend** — Full-stack experience

---

## 👨‍💻 Author

<div align="center">

**Ahmed Nawar** — Backend Engineer · Java & Spring Boot Specialist

[![LinkedIn](https://img.shields.io/badge/LinkedIn-Ahmed%20Nawar-0077B5?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/ahmed-nawar-246513243)
[![GitHub](https://img.shields.io/badge/GitHub-AhmedNawar2003-181717?style=for-the-badge&logo=github&logoColor=white)](https://github.com/AhmedNawar2003)
[![Email](https://img.shields.io/badge/Email-nawarahmed652%40gmail.com-D14836?style=for-the-badge&logo=gmail&logoColor=white)](mailto:nawarahmed652@gmail.com)

<br/>

⭐ **If you find this project useful, please give it a star — it means a lot!**

</div>
