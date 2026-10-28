package com.example.inventory_service.controller;

import com.example.inventory_service.dto.InventoryRequestDTO;
import com.example.inventory_service.dto.InventoryResponseDTO;
import com.example.inventory_service.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("inventory")
public class InventoryController {
    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping
    public InventoryResponseDTO addQuantityOfProduct(@Valid @RequestBody InventoryRequestDTO inventoryRequestDTO){
        return inventoryService.addQuantityOfProduct(inventoryRequestDTO);
    }

    @GetMapping
    public List<InventoryResponseDTO> getQuantitiesOfProducts(){
        return inventoryService.getQuantitiesOfProducts();
    }
}
