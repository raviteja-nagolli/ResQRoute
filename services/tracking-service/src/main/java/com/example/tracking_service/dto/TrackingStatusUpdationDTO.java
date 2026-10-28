package com.example.tracking_service.dto;

public class TrackingStatusUpdationDTO {
    private Integer orderId;

    private String orderStatus;

    public TrackingStatusUpdationDTO() {
    }

    public TrackingStatusUpdationDTO(Integer orderId, String orderStatus) {
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
