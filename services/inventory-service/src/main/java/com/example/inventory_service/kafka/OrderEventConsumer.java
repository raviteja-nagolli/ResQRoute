package com.example.inventory_service.kafka;

import com.example.inventory_service.event.OrderCancelledEvent;
import com.example.inventory_service.event.OrderCreatedEvent;
import com.example.inventory_service.service.InventoryService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class OrderEventConsumer {
    private final InventoryService inventoryService;
    private static final Logger log= LoggerFactory.getLogger(OrderEventConsumer.class);
    private static final String CORRELATION_ID="X-Correlation-ID";

    public OrderEventConsumer(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @KafkaListener(topics = "order-created",groupId = "inventory-service-group")
    public void consumeOrder(ConsumerRecord<String, OrderCreatedEvent> record){
        Header header =record.headers().lastHeader(CORRELATION_ID);
        String correlationId=new String(header.value(), StandardCharsets.UTF_8);
        MDC.put(CORRELATION_ID,correlationId);
        try {
            log.info("Order placed: orderId={}",record.value().getOrderId());
            inventoryService.updateInventoryOnBasisOfOrder(record.value());
        }finally {
            MDC.remove(CORRELATION_ID);
        }
    }

    @KafkaListener(topics = "order-cancelled",groupId = "inventory-service-group")
    public void cancelOrder(ConsumerRecord<String, OrderCancelledEvent> record){
        Header header=record.headers().lastHeader(CORRELATION_ID);
        String correlationId=new String(header.value(), StandardCharsets.UTF_8);
        MDC.put(CORRELATION_ID,correlationId);
        try {
            log.info("Order cancelled: orderId={}",record.value().getOrderId());
            inventoryService.cancelOrder(record.value());
        }finally {
            MDC.remove(CORRELATION_ID);
        }
    }
}
