package com.example.tracking_service.dto;

import java.time.LocalDateTime;

public class TrackingHistoryCreationDTO {
    private Integer orderId;
    private String orderLocation;
    private String orderStatus;
    private LocalDateTime timeStamp;

    public TrackingHistoryCreationDTO() {
    }

    public TrackingHistoryCreationDTO(Integer orderId, String orderLocation, String orderStatus, LocalDateTime timeStamp) {
        this.orderId = orderId;
        this.orderLocation = orderLocation;
        this.orderStatus = orderStatus;
        this.timeStamp = timeStamp;
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

    public String getOrderLocation() {
        return orderLocation;
    }

    public void setOrderLocation(String orderLocation) {
        this.orderLocation = orderLocation;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }
}
