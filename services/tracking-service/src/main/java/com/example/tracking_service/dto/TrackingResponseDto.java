package com.example.tracking_service.dto;

import java.time.LocalDateTime;

public class TrackingResponseDto {
    private Integer trackingId;
    private Integer orderId;
    private String orderStatus;
    private String orderLocation;
    private LocalDateTime trackingUpdatedAt;

    public TrackingResponseDto() {
    }

    public TrackingResponseDto(Integer trackingId, Integer orderId, String orderStatus, String orderLocation, LocalDateTime trackingUpdatedAt) {
        this.trackingId = trackingId;
        this.orderId = orderId;
        this.orderStatus = orderStatus;
        this.orderLocation = orderLocation;
        this.trackingUpdatedAt = trackingUpdatedAt;
    }

    public Integer getTrackingId() {
        return trackingId;
    }

    public void setTrackingId(Integer trackingId) {
        this.trackingId = trackingId;
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

    public LocalDateTime getTrackingUpdatedAt() {
        return trackingUpdatedAt;
    }

    public void setTrackingUpdatedAt(LocalDateTime trackingUpdatedAt) {
        this.trackingUpdatedAt = trackingUpdatedAt;
    }
}
