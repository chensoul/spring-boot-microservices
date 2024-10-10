# Spring Boot Microservices

> ATTENTION: This repository is archived, you can find the source code in the new repository that includes much more
> concepts and upto date - https://github.com/SaiUpadhyayula/spring-boot-3-microservices-course

This repository contains the latest source code of the spring-boot-microservices tutorial

You can watch the tutorial on Youtube [here](https://www.youtube.com/watch?v=mPPhcU7oWDU&t=20634s)

## Services Overview

- Product Service
- Order Service
- Inventory Service
- Notification Service
- Gateway using Spring Cloud Gateway
- Authorization using Spring Security OAuth2
- Service Discovery using Eureka

## Tech Stack

The technologies used in this project are:

- Java: 21
- Maven: 3.9.9
- Spring Boot: 3.3.4
- Spring Cloud Open Feign: [Synchronous Inter-Service Communication Pattern](https://microservices.io/patterns/communication-style/rpi.html) 
- Spring Cloud Netflix Eureka: [Service Discovery Pattern](https://microservices.io/patterns/server-side-discovery.html)
- Spring Cloud Gateway: [API Gateway Pattern](https://microservices.io/patterns/apigateway.html) 
- Spring Security OAuth2: [Microservices Security](https://microservices.io/patterns/security/access-token.html)
- Spring Cloud CircuitBreaker with Resilience4J: [Circuit Breaker Pattern](https://microservices.io/patterns/reliability/circuit-breaker.html)
- Kafka: [Event Driven Architecture Pattern](https://microservices.io/patterns/data/event-driven-architecture.html)
- Grafana Stack (Prometheus, Grafana, Loki and Tempo): [Distributed Tracing](https://microservices.io/patterns/observability/distributed-tracing.html)、[Monitoring](https://microservices.io/patterns/observability/monitoring.html)、[Logging](https://microservices.io/patterns/observability/application-logging.html)
- MySQL: 8
- Spring Data MongoDB
- Test Containers with Wiremock
- Docker Compose

## How to run the services using Docker

1. Run the following command to build and package the backend services into a docker container

```shell
mvn spring-boot:build-image -DskipTests
```

If you want to push the docker images to your docker hub account, you can use the following command.

```shell
mvn spring-boot:build-image -DskipTests \
  -Ddocker.publishRegistry.username=user \
  -Ddocker.publishRegistry.password=secret \
  -Dspring-boot.build-image.publish=true
```

The above command will build and package the services into a docker container and push it to your docker hub account.

2. Run the following command to start the applications.

```shell
docker-compose up -d
```

## How to run the services without Docker

1. Run `mvn clean verify -DskipTests` in the project to build the applications.

2. After that run `mvn spring-boot:run` by going inside each folder to start the applications.
