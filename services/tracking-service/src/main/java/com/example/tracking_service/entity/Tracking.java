package com.example.tracking_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class Tracking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer trackingId;

    private Integer orderId;

    private String orderStatus;

    private String orderLocation;

    private LocalDateTime trackingUpdatedAt;

    public Tracking() {
    }

    public Tracking(Integer trackingId, Integer orderId, String orderStatus, String orderLocation, LocalDateTime trackingUpdatedAt) {
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
