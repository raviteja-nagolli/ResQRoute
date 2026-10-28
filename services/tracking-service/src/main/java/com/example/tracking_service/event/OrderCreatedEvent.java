package com.example.tracking_service.event;

public class OrderCreatedEvent {
    private Integer orderId;
    private Integer productId;
    private Integer orderQuantity;
    private Double totalPrice;

    public OrderCreatedEvent() {
    }

    public OrderCreatedEvent(Integer orderId, Integer productId, Integer orderQuantity, Double totalPrice) {
        this.orderId = orderId;
        this.productId = productId;
        this.orderQuantity = orderQuantity;
        this.totalPrice = totalPrice;
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
}
