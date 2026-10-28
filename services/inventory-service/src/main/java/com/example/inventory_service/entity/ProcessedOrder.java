package com.example.inventory_service.entity;

import jakarta.persistence.*;

@Entity
public class ProcessedOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer processedOrderId;

    @Column(nullable = false, unique = true)
    private Integer orderId;

    public ProcessedOrder() {
    }

    public ProcessedOrder(Integer processedOrderId, Integer orderId) {
        this.processedOrderId = processedOrderId;
        this.orderId = orderId;
    }

    public Integer getProcessedOrderId() {
        return processedOrderId;
    }

    public void setProcessedOrderId(Integer processedOrderId) {
        this.processedOrderId = processedOrderId;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }
}
