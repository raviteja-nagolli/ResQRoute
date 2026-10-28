package com.example.notification_service.kafka;

import com.example.notification_service.dto.NotificationCreationDTO;
import com.example.notification_service.enums.NotificationStatus;
import com.example.notification_service.enums.NotificationType;
import com.example.notification_service.event.OrderCancelledEvent;
import com.example.notification_service.event.OrderCreatedEvent;
import com.example.notification_service.event.OrderUpdatedEvent;
import com.example.notification_service.service.NotificationService;
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
    private final NotificationService notificationService;
    private static final Logger log= LoggerFactory.getLogger(OrderEventConsumer.class);

    private static final String CORRELATION_ID="X-Correlation-ID";

    public OrderEventConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @KafkaListener(topics = "order-created",groupId = "notification-service-group")
    public void consumeOrderCreatedEvent(ConsumerRecord<String, OrderCreatedEvent> record){
        Header header=record.headers().lastHeader(CORRELATION_ID);
        String correlationId=new String(header.value(), StandardCharsets.UTF_8);
        MDC.put(CORRELATION_ID,correlationId);
        try {
            OrderCreatedEvent event=record.value();
            log.info("Order placed: orderId={}",event.getOrderId());
            NotificationCreationDTO notificationCreationDTO=new NotificationCreationDTO();
            notificationCreationDTO.setOrderId(event.getOrderId());
            notificationCreationDTO.setNotificationType(NotificationType.PLACED.name());
            notificationCreationDTO.setNotificationMessage("Order with id "+event.getOrderId()+" has been placed.");
            notificationService.createNotification(notificationCreationDTO);
        }finally {
            MDC.remove(CORRELATION_ID);
        }
    }

    @KafkaListener(topics = "order-cancelled",groupId = "notification-service-group")
    public void consumerOrderCancelledEvent(ConsumerRecord<String, OrderCancelledEvent> record){
        Header header=record.headers().lastHeader(CORRELATION_ID);
        String correlationId=new String(header.value(), StandardCharsets.UTF_8);
        MDC.put(CORRELATION_ID,correlationId);
        try {
            OrderCancelledEvent event=record.value();
            log.info("Order cancelled: orderId{}",event.getOrderId());
            NotificationCreationDTO notificationCreationDTO=new NotificationCreationDTO();
            notificationCreationDTO.setOrderId(event.getOrderId());
            notificationCreationDTO.setNotificationType(NotificationType.CANCELLED.name());
            notificationCreationDTO.setNotificationMessage("Order with id "+event.getOrderId()+" has been cancelled.");
            notificationService.createNotification(notificationCreationDTO);
        }finally {
            MDC.remove(CORRELATION_ID);
        }
    }

    @KafkaListener(topics = "order-updated",groupId = "notification-service-group")
    public void consumerOrderUpdatedEvent(ConsumerRecord<String, OrderUpdatedEvent> record){
        Header header=record.headers().lastHeader(CORRELATION_ID);
        String correlationId=new String(header.value(), StandardCharsets.UTF_8);
        MDC.put(CORRELATION_ID,correlationId);
        try {
            OrderUpdatedEvent event=record.value();
            NotificationCreationDTO notificationCreationDTO=new NotificationCreationDTO();
            notificationCreationDTO.setOrderId(event.getOrderId());
            notificationCreationDTO.setNotificationType(event.getOrderStatus());
            String message=null;
            if(event.getOrderStatus().equals(NotificationType.CONFIRMED.name())){
                message="confirmed.";
            }
            else if(event.getOrderStatus().equals(NotificationType.SHIPPED.name())){
                message="shipped.";
            }
            else if(event.getOrderStatus().equals(NotificationType.DELIVERED.name())){
                message="delivered.";
            }
            else if(event.getOrderStatus().equals(NotificationType.CANCELLED.name())){
                message="cancelled.";
            }
            notificationCreationDTO.setNotificationMessage("Order with id "+event.getOrderId()+" has been "+message);
            notificationService.createNotification(notificationCreationDTO);
            log.info("Order Status Updated: orderId={}, orderStatus={}",event.getOrderId(),event.getOrderStatus());
        }
        finally {
            MDC.remove(CORRELATION_ID);
        }
    }
}