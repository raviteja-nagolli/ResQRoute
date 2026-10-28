package com.example.order_service.controller;

import com.example.order_service.client.ProductClient;
import com.example.order_service.dto.OrderRequestDTO;
import com.example.order_service.dto.OrderResponseDTO;
import com.example.order_service.dto.OrderStatusUpdateDTO;
import com.example.order_service.dto.ProductResponseDTO;
import com.example.order_service.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("order")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/product/{id}")
    public ProductResponseDTO getProduct(@PathVariable Integer id){
        return orderService.getProduct(id);
    }

    @PostMapping()
    public OrderResponseDTO placeOrder(@Valid @RequestBody OrderRequestDTO orderRequestDTO){
        return orderService.placeOrder(orderRequestDTO);
    }

    @GetMapping("/{id}")
    public OrderResponseDTO getOrderById(@PathVariable Integer id){
        return orderService.getOrderById(id);
    }

    @GetMapping()
    public List<OrderResponseDTO> getAllOrders(){
        return orderService.getAllOrders();
    }

    @PutMapping("/{id}")
    public OrderResponseDTO updateOrder(@PathVariable Integer id, @Valid @RequestBody OrderRequestDTO orderRequestDTO){
        return orderService.updateOrder(id,orderRequestDTO);
    }

    @DeleteMapping("/{id}")
    public String deleteOrder(@PathVariable Integer id){
        return orderService.deleteOrder(id);
    }

    @PutMapping("/{id}/status")
    public OrderResponseDTO updateOrderStatus(@PathVariable Integer id, @RequestBody OrderStatusUpdateDTO orderStatusUpdateDTO){
        return orderService.updateOrderStatus(id,orderStatusUpdateDTO);
    }

    @PutMapping("/cancel/{id}")
    public OrderResponseDTO cancelOrder(@PathVariable Integer id){
        return orderService.cancelOrder(id);
    }
}
