package com.example.tracking_service.kafka;

import com.example.tracking_service.dto.TrackingUpdationDto;
import com.example.tracking_service.event.InventoryFailedEvent;
import com.example.tracking_service.event.InventoryReservedEvent;
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
public class InventoryEventConsumer {
    private static final Logger log= LoggerFactory.getLogger(InventoryEventConsumer.class);
    private static final String CORRELATION_ID="X-Correlation-ID";
    private final TrackingService trackingService;

    public InventoryEventConsumer(TrackingService trackingService) {
        this.trackingService = trackingService;
    }

//    @KafkaListener(topics = "inventory-reserved",groupId = "tracking-service-group")
//    public void consumeInventoryReservedEvent(ConsumerRecord<String, InventoryReservedEvent> record){
//        Header header=record.headers().lastHeader(CORRELATION_ID);
//        String correlationId=new String(header.value(), StandardCharsets.UTF_8);
//        MDC.put(CORRELATION_ID,correlationId);
//        try{
//            log.info("Inventory reserved: orderId={}",record.value().getOrderId());
//            TrackingUpdationDto trackingUpdationDto=new TrackingUpdationDto();
//            trackingUpdationDto.setOrderId(record.value().getOrderId());
//            trackingService.updateTrackingForIR(trackingUpdationDto);
//        }finally {
//            MDC.remove(CORRELATION_ID);
//        }
//    }

    @KafkaListener(topics = "inventory-failed", groupId = "tracking-service-group")
    public void consumeInventoryFailedEvent(ConsumerRecord<String, InventoryFailedEvent> record){
        Header header=record.headers().lastHeader(CORRELATION_ID);
        String correlationId=new String(header.value(), StandardCharsets.UTF_8);
        MDC.put(CORRELATION_ID,correlationId);
        try {
            log.info("Inventory failed: orderId={}",record.value().getOrderId());
            TrackingUpdationDto trackingUpdationDto =new TrackingUpdationDto();
            trackingUpdationDto.setOrderId(record.value().getOrderId());
            trackingService.updateTrackingForCancelled(trackingUpdationDto);
        }finally {
            MDC.remove(CORRELATION_ID);
        }
    }
}
