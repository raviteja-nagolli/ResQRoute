package com.example.order_service.kafka;

import com.example.order_service.dto.OrderStatusUpdateDTO;
import com.example.order_service.enums.OrderStatus;
import com.example.order_service.event.InventoryFailedEvent;
import com.example.order_service.event.InventoryReservedEvent;
import com.example.order_service.service.OrderService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class InventoryEventConsumer {
    private final OrderService orderService;
    private final static Logger log= LoggerFactory.getLogger(InventoryEventConsumer.class);
    private final static String CORRELATION_ID="X-Correlation-ID";

    public InventoryEventConsumer(OrderService orderService) {
        this.orderService = orderService;
    }

    @KafkaListener(topics = "inventory-failed",groupId = "order-service-inventory-group")
    public void consumeInventoryFailedEvent(ConsumerRecord<String, InventoryFailedEvent> record){
        InventoryFailedEvent event=record.value();
        Header header=record.headers().lastHeader(CORRELATION_ID);
        String correlationId=new String(header.value(), StandardCharsets.UTF_8);
        MDC.put(CORRELATION_ID,correlationId);
        try {
            log.info("Inventory reservation failed: Order cancelled: orderId={}",event.getOrderId());
            OrderStatusUpdateDTO orderStatusUpdateDTO=new OrderStatusUpdateDTO();
            orderStatusUpdateDTO.setOrderStatus(OrderStatus.CANCELLED.name());
            orderService.updateOrderStatus(event.getOrderId(),orderStatusUpdateDTO);
        }finally {
            MDC.remove(CORRELATION_ID);
        }
    }

    @KafkaListener(topics = "inventory-reserved",groupId = "order-service-inventory-group")
    public void consumeInventoryReservedEvent(ConsumerRecord<String, InventoryReservedEvent> record){
        InventoryReservedEvent event=record.value();
        Header header=record.headers().lastHeader(CORRELATION_ID);
        String correlationId=new String(header.value(), StandardCharsets.UTF_8);
        MDC.put(CORRELATION_ID,correlationId);
        try {
            log.info("Inventory reserved: orderId={}",event.getOrderId());
            OrderStatusUpdateDTO orderStatusUpdateDTO = new OrderStatusUpdateDTO();
            orderStatusUpdateDTO.setOrderStatus(OrderStatus.CONFIRMED.name());
            orderService.updateOrderStatus(event.getOrderId(), orderStatusUpdateDTO);
        }finally {
            MDC.remove(CORRELATION_ID);
        }
    }
}
