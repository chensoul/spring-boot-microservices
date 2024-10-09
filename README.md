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
- API Gateway using Spring Cloud Gateway MVC

## Tech Stack

The technologies used in this project are:

- Spring Boot 3.3.4
- Mongo DB
- MySQL
- Kafka
- Test Containers with Wiremock
- Grafana Stack (Prometheus, Grafana, Loki and Tempo)
- API Gateway using Spring Cloud Gateway MVC
- Kubernetes

## How to run the services using Docker

1. Run the following command to build and package the backend services into a docker container

```shell
mvn spring-boot:build-image -DdockerPassword=<your-docker-account-password>
```

Or using jib maven plugin build the docker image and push them to your docker hub account.

```shell
mvn jib:dockerBuild
docker login
docker push chensoul/authorization:latest
docker push chensoul/eureka:latest
docker push chensoul/gateway:latest
docker push chensoul/product-service:latest
docker push chensoul/order-service:latest
docker push chensoul/inventory-service:latest
docker push chensoul/notification-service:latest
```

The above command will build and package the services into a docker container and push it to your docker hub account.

2. Run the following command to start the applications.

```shell
docker-compose up -d
```

## How to run the services without Docker

1. Run `mvn clean verify -DskipTests` by going inside each folder to build the applications.
2. After that run `mvn spring-boot:run` by going inside each folder to start the applications.

## How to run the services using K8s

Make sure you have the following installed on your machine:

- Java 21
- Docker
- Kind Cluster - https://kind.sigs.k8s.io/docs/user/quick-start/#installation

### Start Kind Cluster

Run the k8s/kind/create-kind-cluster.sh script to create the kind Kubernetes cluster

```shell
./k8s/kind/create-kind-cluster.sh
```

This will create a kind cluster and pre-load all the required docker images into the cluster, this will save you time
downloading the images when you deploy the application.

### Deploy the infrastructure

Run the k8s/manisfests/infrastructure.yaml file to deploy the infrastructure

```shell
kubectl apply -f k8s/manifests/infrastructure.yaml
```

### Deploy the services

Run the k8s/manifests/applications.yaml file to deploy the services

```shell
kubectl apply -f k8s/manifests/applications.yaml
```

### Access the API Gateway

To access the API Gateway, you need to port-forward the gateway service to your local machine

```shell
kubectl port-forward svc/api-gateway 9000:9000
```

### Access the Grafana Dashboards

To access the Grafana dashboards, you need to port-forward the grafana service to your local machine

```shell
kubectl port-forward svc/grafana 3000:3000
```
