docker pull mongo:7
docker pull mysql:8
docker pull confluentinc/cp-zookeeper
docker pull confluentinc/cp-kafka
docker pull prom/prometheus
docker pull grafana/grafana
docker pull chensoul/authorization:latest
docker pull chensoul/eureka:latest
docker pull chensoul/gateway:latest
docker pull chensoul/product-service:latest
docker pull chensoul/order-service:latest
docker pull chensoul/inventory-service:latest
docker pull chensoul/notification-service:latest

kind load docker-image -n microservices mongo:7
kind load docker-image -n microservices mysql:8
kind load docker-image -n microservices confluentinc/cp-zookeeper
kind load docker-image -n microservices confluentinc/cp-kafka
kind load docker-image -n microservices prom/prometheus
kind load docker-image -n microservices grafana/grafana
kind load docker-image -n microservices chensoul/authorization:latest
kind load docker-image -n microservices chensoul/eureka:latest
kind load docker-image -n microservices chensoul/gateway:latest
kind load docker-image -n microservices chensoul/product-service:latest
kind load docker-image -n microservices chensoul/order-service:latest
kind load docker-image -n microservices chensoul/inventory-service:latest
kind load docker-image -n microservices chensoul/notification-service:latest
