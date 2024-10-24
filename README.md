# Spring Boot Microservices

> ATTENTION: This repository is archived, you can find the source code in the new repository that includes much more
> concepts and up to date - https://github.com/SaiUpadhyayula/spring-boot-3-microservices-course

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
- MySQL: 9.1
- MongoDB: 8.0.1
- Kafka: [Event Driven Architecture Pattern](https://microservices.io/patterns/data/event-driven-architecture.html)
- Spring Boot: 3.3.4
- Spring Cloud OpenFeign
  Feign: [Synchronous Inter-Service Communication Pattern](https://microservices.io/patterns/communication-style/rpi.html)
- Spring Cloud Netflix Eureka: [Service Discovery Pattern](https://microservices.io/patterns/server-side-discovery.html)
- Spring Cloud Gateway: [API Gateway Pattern](https://microservices.io/patterns/apigateway.html)
- Spring Security OAuth2: [Microservices Security](https://microservices.io/patterns/security/access-token.html)
- Spring Cloud CircuitBreaker with
  Resilience4J: [Circuit Breaker Pattern](https://microservices.io/patterns/reliability/circuit-breaker.html)
- Grafana Stack (Prometheus, Grafana): [Monitoring](https://microservices.io/patterns/observability/monitoring.html)
- Zipkin: [Distributed Tracing](https://microservices.io/patterns/observability/distributed-tracing.html)
- Test Containers with Wiremock
- Docker Compose

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
docker-compose -f docker-compose.yml docker-compose-app.yml docker-compose-zipkin.yml docker-compose-monitor.yml up -d
```

## How to run the services without Docker

1. Run `mvn clean verify -DskipTests` in the project to build the applications.

2. After that run `mvn spring-boot:run` by going inside each folder to start the applications.
