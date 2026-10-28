package com.example.inventory_service.dto;

public class InventoryResponseDTO {
    private Integer inventoryId;
    private Integer productId;
    private Integer availableQuantity;

    public InventoryResponseDTO() {
    }

    public InventoryResponseDTO(Integer inventoryId, Integer productId, Integer availableQuantity) {
        this.inventoryId = inventoryId;
        this.productId = productId;
        this.availableQuantity = availableQuantity;
    }

    public Integer getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(Integer inventoryId) {
        this.inventoryId = inventoryId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(Integer availableQuantity) {
        this.availableQuantity = availableQuantity;
    }
}
