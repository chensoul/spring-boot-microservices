package com.programmingtechie.orderservice.service;

import com.programmingtechie.orderservice.dto.InventoryResponse;
import com.programmingtechie.orderservice.dto.OrderRequest;
import com.programmingtechie.orderservice.event.OrderPlacedEvent;
import com.programmingtechie.orderservice.model.Order;
import com.programmingtechie.orderservice.repository.OrderRepository;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderService {

  private final OrderRepository orderRepository;
  private final WebClient.Builder webClientBuilder;
  private final ObservationRegistry observationRegistry;
  private final ApplicationEventPublisher applicationEventPublisher;

  public String placeOrder(OrderRequest orderRequest) {
    // Call Inventory Service, and place order if product is in stock
    Observation inventoryServiceObservation = Observation.createNotStarted("inventory-service-lookup",
            this.observationRegistry);
    inventoryServiceObservation.lowCardinalityKeyValue("call", "inventory-service");
    return inventoryServiceObservation.observe(() -> {
      InventoryResponse[] inventoryResponseArray = webClientBuilder.build().get()
              .uri("http://inventory-service/api/inventory",
                      uriBuilder -> uriBuilder.queryParam("skuCode", orderRequest.getSkuCode()).build())
              .retrieve()
              .bodyToMono(InventoryResponse[].class)
              .block();

      boolean allProductsInStock = Arrays.stream(inventoryResponseArray).allMatch(InventoryResponse::isInStock);

      if (allProductsInStock) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        order.setPrice(orderRequest.getPrice().multiply(BigDecimal.valueOf(orderRequest.getQuantity())));
        order.setSkuCode(orderRequest.getSkuCode());
        order.setQuantity(orderRequest.getQuantity());
        orderRepository.save(order);
        // publish Order Placed Event
        applicationEventPublisher.publishEvent(new OrderPlacedEvent(this, order.getOrderNumber()));
        return "Order Placed";
      } else {
        throw new IllegalArgumentException("Product is not in stock, please try again later");
      }
    });
  }
}
