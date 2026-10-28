package com.example.inventory_service.kafka;

import com.example.inventory_service.event.InventoryFailedEvent;
import com.example.inventory_service.event.InventoryReservedEvent;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.MDC;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class InventoryEventProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String CORRELATION_ID="X-Correlation-ID";

    public InventoryEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendInventoryFailedEvent(InventoryFailedEvent inventoryFailedEvent){
        String correlationId= MDC.get(CORRELATION_ID);
        ProducerRecord<String, Object> record=new ProducerRecord<>("inventory-failed",inventoryFailedEvent);
        if (correlationId!=null && !correlationId.isBlank()){
            record.headers().add(CORRELATION_ID,correlationId.getBytes(StandardCharsets.UTF_8));
        }
        kafkaTemplate.send(record);
    }

    public void sendInventoryReservedEvent(InventoryReservedEvent inventoryReservedEvent){
        String correlationId=MDC.get(CORRELATION_ID);
        ProducerRecord<String, Object> record=new ProducerRecord<>("inventory-reserved",inventoryReservedEvent);
        if (correlationId!=null && !correlationId.isBlank()){
            record.headers().add(CORRELATION_ID,correlationId.getBytes(StandardCharsets.UTF_8));
        }
        kafkaTemplate.send(record);
    }
}
