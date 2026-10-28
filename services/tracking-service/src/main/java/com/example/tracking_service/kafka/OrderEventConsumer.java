package com.example.tracking_service.kafka;

import com.example.tracking_service.dto.TrackingStatusUpdationDTO;
import com.example.tracking_service.dto.TrackingUpdationDto;
import com.example.tracking_service.dto.TrackingCreationDTO;
import com.example.tracking_service.event.OrderCancelledEvent;
import com.example.tracking_service.event.OrderCreatedEvent;
import com.example.tracking_service.event.OrderUpdatedEvent;
import com.example.tracking_service.service.TrackingService;
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
    private static final Logger log= LoggerFactory.getLogger(OrderEventConsumer.class);
    private static final String CORRELATION_ID="X-Correlation-ID";
    private final TrackingService trackingService;

    public OrderEventConsumer(TrackingService trackingService) {
        this.trackingService = trackingService;
    }

    @KafkaListener(topics = "order-created",groupId = "tracking-service-group")
    public void consumeOrderCreatedEvent(ConsumerRecord<String, OrderCreatedEvent> record){
        Header header=record.headers().lastHeader(CORRELATION_ID);
        String correlationId=new String(header.value(), StandardCharsets.UTF_8);
        MDC.put(CORRELATION_ID,correlationId);
        try {
            log.info("Order created: orderId={}",record.value().getOrderId());
            TrackingCreationDTO trackingCreationDTO=new TrackingCreationDTO();
            trackingCreationDTO.setOrderId(record.value().getOrderId());
            trackingService.createNewTracking(trackingCreationDTO);
        }finally {
            MDC.remove(CORRELATION_ID);
        }
    }

    @KafkaListener(topics = "order-cancelled",groupId = "tracking-service-group")
    public void consumeOrderCancelledEvent(ConsumerRecord<String, OrderCancelledEvent> record){
        Header header=record.headers().lastHeader(CORRELATION_ID);
        String correlationId=new String(header.value(), StandardCharsets.UTF_8);
        MDC.put(CORRELATION_ID,correlationId);
        try {
            log.info("Order cancelled: orderId={}",record.value().getOrderId());
            TrackingUpdationDto trackingUpdationDto =new TrackingUpdationDto();
            trackingUpdationDto.setOrderId(record.value().getOrderId());
            trackingService.updateTrackingForCancelled(trackingUpdationDto);
        }finally {
            MDC.remove(CORRELATION_ID);
        }
    }

    @KafkaListener(topics = "order-updated",groupId = "tracking-service-group")
    public void consumerOrderUpdatedEvent(ConsumerRecord<String, OrderUpdatedEvent> record){
        Header header=record.headers().lastHeader(CORRELATION_ID);
        String correlationId=new String(header.value(), StandardCharsets.UTF_8);
        MDC.put(CORRELATION_ID,correlationId);
        try {
            log.info("Order Status Updated: orderId={}",record.value().getOrderId());
            TrackingStatusUpdationDTO trackingStatusUpdationDTO=new TrackingStatusUpdationDTO();
            trackingStatusUpdationDTO.setOrderId(record.value().getOrderId());
            trackingStatusUpdationDTO.setOrderStatus(record.value().getOrderStatus());
            trackingService.updateTrackingForStatus(trackingStatusUpdationDTO);
        }finally {
            MDC.remove(CORRELATION_ID);
        }
    }
}
