package com.example.order_service.event;

public class InventoryFailedEvent {
    private Integer orderId;
    private Integer productId;
    private Integer orderQuantity;
    private String reason;

    public InventoryFailedEvent() {
    }

    public InventoryFailedEvent(Integer orderId, Integer productId, Integer orderQuantity, String reason) {
        this.orderId = orderId;
        this.productId = productId;
        this.orderQuantity = orderQuantity;
        this.reason = reason;
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

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
