package com.example.order_service.dto;

import java.time.LocalDateTime;

public class OrderResponseDTO {
    private Integer orderId;
    private Integer productId;
    private Integer orderQuantity;
    private Double totalPrice;
    private String orderStatus;
    private LocalDateTime orderCreatedAt;

    public OrderResponseDTO() {
    }

    public OrderResponseDTO(Integer orderId, Integer productId, Integer orderQuantity, Double totalPrice, String orderStatus, LocalDateTime orderCreatedAt) {
        this.orderId = orderId;
        this.productId = productId;
        this.orderQuantity = orderQuantity;
        this.totalPrice = totalPrice;
        this.orderStatus = orderStatus;
        this.orderCreatedAt = orderCreatedAt;
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

    public Integer getOrderQuantity() {
        return orderQuantity;
    }

    public void setOrderQuantity(Integer orderQuantity) {
        this.orderQuantity = orderQuantity;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public LocalDateTime getOrderCreatedAt() {
        return orderCreatedAt;
    }

    public void setOrderCreatedAt(LocalDateTime orderCreatedAt) {
        this.orderCreatedAt = orderCreatedAt;
    }
}
