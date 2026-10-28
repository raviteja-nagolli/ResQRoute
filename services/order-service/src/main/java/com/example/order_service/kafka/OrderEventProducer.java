package com.example.order_service.kafka;

import com.example.order_service.event.OrderCancelledEvent;
import com.example.order_service.event.OrderCreatedEvent;
import com.example.order_service.event.OrderUpdatedEvent;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.MDC;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class OrderEventProducer {
    private static final String CORRELATION_ID="X-Correlation-ID";
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public OrderEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendOrders(OrderCreatedEvent orderCreatedEvent){
        String correlationId= MDC.get(CORRELATION_ID);
        ProducerRecord<String, Object> record=new ProducerRecord<>("order-created","Order: "+orderCreatedEvent.getOrderId(),orderCreatedEvent);
        if (correlationId!=null && !correlationId.isBlank()){
            record.headers().add(CORRELATION_ID,correlationId.getBytes(StandardCharsets.UTF_8));
        }
        kafkaTemplate.send(record);
    }

    public void sendCancelledOrder(OrderCancelledEvent orderCancelledEvent){
        String correlationId=MDC.get(CORRELATION_ID);
        ProducerRecord<String, Object> record=new ProducerRecord<>("order-created",orderCancelledEvent);
        if(correlationId!=null && !correlationId.isBlank()){
            record.headers().add(CORRELATION_ID,correlationId.getBytes(StandardCharsets.UTF_8));
        }
        kafkaTemplate.send(record);
    }

    public void sendOrderStatusUpdate(OrderUpdatedEvent orderUpdatedEvent){
        String correlationId=MDC.get(CORRELATION_ID);
        ProducerRecord<String, Object> record=new ProducerRecord<>("order-updated",orderUpdatedEvent);
        if (correlationId!=null && !correlationId.isBlank()){
            record.headers().add(CORRELATION_ID,correlationId.getBytes(StandardCharsets.UTF_8));
        }
        kafkaTemplate.send(record);
    }
}
