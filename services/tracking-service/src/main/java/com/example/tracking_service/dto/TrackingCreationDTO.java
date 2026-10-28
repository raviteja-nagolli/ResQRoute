package com.example.tracking_service.dto;

import com.example.tracking_service.enums.OrderStatus;

import java.time.LocalDateTime;

public class TrackingCreationDTO {
    private Integer orderId;

    public TrackingCreationDTO() {
    }

    public TrackingCreationDTO(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }
}
