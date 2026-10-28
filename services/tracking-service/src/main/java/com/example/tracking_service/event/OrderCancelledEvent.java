package com.example.tracking_service.event;

public class OrderCancelledEvent {
    private Integer orderId;
    private Integer productId;
    private Integer orderQuantity;

    public OrderCancelledEvent() {
    }

    public OrderCancelledEvent(Integer orderId, Integer productId, Integer orderQuantity) {
        this.orderId = orderId;
        this.productId = productId;
        this.orderQuantity = orderQuantity;
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
}
