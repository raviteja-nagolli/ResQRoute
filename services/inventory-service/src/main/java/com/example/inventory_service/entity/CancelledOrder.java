package com.example.inventory_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class CancelledOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer cancelledOrderId;

    private Integer orderId;
    private Integer productId;

    public CancelledOrder() {
    }

    public CancelledOrder(Integer cancelledOrderId, Integer orderId, Integer productId) {
        this.cancelledOrderId = cancelledOrderId;
        this.orderId = orderId;
        this.productId = productId;
    }

    public Integer getCancelledOrderId() {
        return cancelledOrderId;
    }

    public void setCancelledOrderId(Integer cancelledOrderId) {
        this.cancelledOrderId = cancelledOrderId;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }
}
