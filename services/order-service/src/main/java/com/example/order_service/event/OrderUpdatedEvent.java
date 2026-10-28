package com.example.order_service.event;

public class OrderUpdatedEvent {
    private Integer orderId;
    private String orderStatus;

    public OrderUpdatedEvent() {
    }

    public OrderUpdatedEvent(Integer orderId, String orderStatus) {
        this.orderId = orderId;
        this.orderStatus = orderStatus;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
}
