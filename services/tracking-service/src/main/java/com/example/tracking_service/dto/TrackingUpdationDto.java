package com.example.tracking_service.dto;

public class TrackingUpdationDto {
    private Integer orderId;

    public TrackingUpdationDto() {
    }

    public TrackingUpdationDto(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }
}
