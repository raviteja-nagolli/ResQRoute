package com.example.order_service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class OrderRequestDTO {
    @NotNull
    private Integer productId;
    @Positive
    private Integer orderQuantity;

    public OrderRequestDTO() {
    }

    public OrderRequestDTO(Integer productId, Integer orderQuantity) {
        this.productId = productId;
        this.orderQuantity = orderQuantity;
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
