# Spring Boot Microservices

> ATTENTION: This repository is archived, you can find the source code in the new repository that includes much more
> concepts and up to date - https://github.com/SaiUpadhyayula/spring-boot-3-microservices-course

This repository contains the latest source code of the spring-boot-microservices tutorial

You can watch the tutorial on Youtube [here](https://www.youtube.com/watch?v=mPPhcU7oWDU&t=20634s)

## Modules

- **api-gateway**：This service is an API Gateway to the internal backend services
  - **TechStack**: Spring Boot, Spring Cloud Gateway
- **eureka-server**：This service is a centralized configuration server for all the services
  - TechStack: Spring Boot, Spring Cloud Netflix Eureka
- **inventory-service**：This services provides REST API for managing inventory.
  - **TechStack**: Spring Boot, Spring Data JPA, MySQL, Flywaydb, Prometheus, Zipkin
- **notification-service**：This services provides REST API for sending notifications.
  - **TechStack**: Spring Boot, Spring Kafka
- **order-service**：This services provides REST API for managing orders.
  - **TechStack**: Spring Boot, Spring Data JPA, MySQL, Flywaydb, Prometheus, Zipkin, Spring Kafka, Spring Cloud CircuitBreaker, Resilience4J, Spring WebFlux
- **product-service**：This services provides REST API for managing products and catalogs.
  - **TechStack**: Spring Boot, Spring Data JPA, Mongodb, Prometheus, Zipkin

## Tech Stack
* Building Spring Boot REST APIs
* Database Persistence using Spring Data JPA, MySQL, Mongodb, Flyway
* Distributed Tracing using Zipkin
* Event Driven Async Communication using Spring Kafka
* Implementing API Gateway using Spring Cloud Gateway
* Implementing Resiliency using Resilience4j
* Using WebClient, Declarative HTTP Interfaces to invoke other APIs
* Local Development Setup using Docker, Docker Compose and Testcontainers
* Monitoring & Observability using Grafana, Prometheus, Loki
* Testing using JUnit 5, RestAssured, Testcontainers, Awaitility, WireMock

## Local Development Setup
- Install Java 21 and Maven 3. Recommend using [SDKMAN](https://sdkman.io/).
- Install [Docker](https://www.docker.com/). Recommend using [OrbStack](https://orbstack.dev/) for Macos. 
- Install [IntelliJ](https://www.jetbrains.com/idea) IDEA or any of your favorite IDE
- Install [Postman](https://www.postman.com/) or any REST Client

## How to run the services using Docker

1. Run the following command to build and package the backend services into a docker container

```shell
BP_JVM_VERSION=21
mvn spring-boot:build-image -DskipTests \
  -Dspring-boot.build-image.imageName=\${env.USER}/\${project.artifactId} \
  -Dspring-boot.build-image.builder=paketobuildpacks/builder-jammy-full 
```

- `BP_JVM_VERSION=21` Using JDK 21 to build the image.
- `spring-boot.build-image.imageName` is the name of the docker image that you want to build.
- `spring-boot.build-image.builder` is the builder that you want to use to build the docker image.
  `paketobuildpacks/builder-jammy-full` image contains the curl command.

If you want to push the docker images to your docker hub account, you can use the following command.

```shell
BP_JVM_VERSION=21
mvn spring-boot:build-image -DskipTests \
  -Dspring-boot.build-image.imageName=\${env.USER}/\${project.artifactId} \
  -Dspring-boot.build-image.builder=paketobuildpacks/builder-jammy-full \
  -Dspring-boot.build-image.publish=true \
  -Ddocker.publishRegistry.username=chensoul \
  -Ddocker.publishRegistry.password=xxxx
```

- `spring-boot.build-image.publish` is a flag that tells maven to push the docker image to your docker hub account.
- `docker.publishRegistry.username` is the username of your docker hub account.
- `docker.publishRegistry.password` is the password of your docker hub account.

The above command will build and package the services into a docker container and push it to your docker hub account.

2. Run the following command to start the applications.

```shell
docker-compose -f docker-compose.yml docker-compose-app.yml up -d
```

## How to run the services without Docker

1. Run `mvn clean verify -DskipTests` in the project to build the applications.

2. After that run `mvn spring-boot:run` by going inside each folder to start the applications.
