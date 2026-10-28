package com.example.order_service.dto;

public class OrderStatusUpdateDTO {
    private String orderStatus;

    public OrderStatusUpdateDTO() {
    }

    public OrderStatusUpdateDTO(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
}
