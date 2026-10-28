package com.example.tracking_service.dto;

import java.time.LocalDateTime;

public class TrackingHistoryResponseDTO {
    private Integer trackingHistoryId;
    private Integer orderId;
    private String orderLocation;
    private String orderStatus;
    private LocalDateTime timeStamp;

    public TrackingHistoryResponseDTO() {
    }

    public TrackingHistoryResponseDTO(Integer trackingHistoryId, Integer orderId, String orderLocation, String orderStatus, LocalDateTime timeStamp) {
        this.trackingHistoryId = trackingHistoryId;
        this.orderId = orderId;
        this.orderLocation = orderLocation;
        this.orderStatus = orderStatus;
        this.timeStamp = timeStamp;
    }

    public Integer getTrackingHistoryId() {
        return trackingHistoryId;
    }

    public void setTrackingHistoryId(Integer trackingHistoryId) {
        this.trackingHistoryId = trackingHistoryId;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getOrderLocation() {
        return orderLocation;
    }

    public void setOrderLocation(String orderLocation) {
        this.orderLocation = orderLocation;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }
}
