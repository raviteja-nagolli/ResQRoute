package com.example.order_service.service;

import com.example.order_service.client.ProductClient;
import com.example.order_service.dto.OrderRequestDTO;
import com.example.order_service.dto.OrderResponseDTO;
import com.example.order_service.dto.OrderStatusUpdateDTO;
import com.example.order_service.dto.ProductResponseDTO;
import com.example.order_service.entity.CustomerOrder;
import com.example.order_service.enums.OrderStatus;
import com.example.order_service.event.OrderCancelledEvent;
import com.example.order_service.event.OrderCreatedEvent;
import com.example.order_service.event.OrderUpdatedEvent;
import com.example.order_service.exception.InvalidOrderStatusTransitionException;
import com.example.order_service.exception.OrderNotFoundException;
import com.example.order_service.kafka.OrderEventProducer;
import com.example.order_service.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductClient productClient;
    private final OrderEventProducer orderEventProducer;
    private static final Logger log= LoggerFactory.getLogger(OrderService.class);

    public OrderService(OrderRepository orderRepository, ProductClient productClient, OrderEventProducer orderEventProducer) {
        this.orderRepository = orderRepository;
        this.productClient = productClient;
        this.orderEventProducer = orderEventProducer;
    }

    private OrderResponseDTO mapToResponse(CustomerOrder order){
        OrderResponseDTO responseDTO=new OrderResponseDTO();
        responseDTO.setOrderId(order.getOrderId());
        responseDTO.setProductId(order.getProductId());
        responseDTO.setOrderQuantity(order.getOrderQuantity());
        responseDTO.setTotalPrice(order.getTotalPrice());
        responseDTO.setOrderStatus(order.getOrderStatus());
        responseDTO.setOrderCreatedAt(order.getOrderCreatedAt());
        return responseDTO;
    }

    public ProductResponseDTO getProduct(Integer id){
        return productClient.getProduct(id);
    }

    public OrderResponseDTO placeOrder(OrderRequestDTO orderRequestDTO){
        log.info("Creating order : productId={}, order quantity={}",orderRequestDTO.getProductId(),orderRequestDTO.getOrderQuantity());
        log.debug("Calling product service : productId={}",orderRequestDTO.getProductId());
        ProductResponseDTO responseDTO=productClient.getProduct(orderRequestDTO.getProductId());
        log.debug("Product response received : productId={}, price={}",responseDTO.getProductId(),responseDTO.getProductPrice());
        CustomerOrder order=new CustomerOrder();
        order.setProductId(responseDTO.getProductId());
        order.setOrderQuantity(orderRequestDTO.getOrderQuantity());
        order.setTotalPrice(orderRequestDTO.getOrderQuantity()*responseDTO.getProductPrice());
        order.setOrderStatus(OrderStatus.PLACED.name());
        order.setOrderCreatedAt(LocalDateTime.now());
        CustomerOrder saved=orderRepository.save(order);
        log.info("Order created successfully : orderId={}",saved.getOrderId());
        OrderCreatedEvent event=new OrderCreatedEvent(saved.getOrderId(), saved.getProductId(), saved.getOrderQuantity(), saved.getTotalPrice());
        log.info("Publishing OrderCreatedEvent : orderId={}",saved.getOrderId());
        orderEventProducer.sendOrders(event);
        return mapToResponse(saved);
    }

//    public OrderResponseDTO updateOrderStatus(InventoryFailedEvent event){
//        CustomerOrder order=orderRepository.findById(event.getOrderId()).orElseThrow(()->new OrderNotFoundException("Order with id "+event.getOrderId()+" not found"));
//        order.setOrderStatus(OrderStatus.CANCELLED.name());
//        CustomerOrder saved=orderRepository.save(order);
//        return mapToResponse(saved);
//    }

    public OrderResponseDTO getOrderById(Integer id){
        CustomerOrder order=orderRepository.findById(id).orElseThrow(()->new OrderNotFoundException("Order with id "+id+" not found"));
        return mapToResponse(order);
    }

    public List<OrderResponseDTO> getAllOrders(){
        return orderRepository.findAll().stream().map(this::mapToResponse).toList();
    }

    public OrderResponseDTO updateOrder(Integer id, OrderRequestDTO orderRequestDTO){
        CustomerOrder order=orderRepository.findById(id).orElseThrow(()->new OrderNotFoundException("Order with id "+id+" not found"));
        ProductResponseDTO responseDTO=productClient.getProduct(orderRequestDTO.getProductId());
        order.setProductId(orderRequestDTO.getProductId());
        order.setOrderQuantity(orderRequestDTO.getOrderQuantity());
        order.setTotalPrice(orderRequestDTO.getOrderQuantity()*responseDTO.getProductPrice());
        CustomerOrder saved=orderRepository.save(order);
        return mapToResponse(saved);
    }

    public String deleteOrder(Integer id){
        CustomerOrder order=orderRepository.findById(id).orElseThrow(()->new OrderNotFoundException("Order with id "+id+" not found"));
        orderRepository.delete(order);
        return "Order with id "+id+" deleted successfully";
    }

    public OrderResponseDTO updateOrderStatus(Integer id, OrderStatusUpdateDTO orderStatusUpdateDTO){
        boolean isValidTransition=true;
        CustomerOrder order=orderRepository.findById(id).orElseThrow(()->new OrderNotFoundException("Order with id "+id+" not found"));
        String currentStatus=order.getOrderStatus();
        String newStatus=orderStatusUpdateDTO.getOrderStatus();
        if(currentStatus.equals(OrderStatus.PLACED.name())){
            if(newStatus.equals(OrderStatus.CONFIRMED.name()) || newStatus.equals(OrderStatus.CANCELLED.name())) {
                order.setOrderStatus(newStatus);
            }
            else {
                isValidTransition=false;
            }
        }
        if (currentStatus.equals(OrderStatus.CONFIRMED.name())){
            if (newStatus.equals(OrderStatus.SHIPPED.name()) || newStatus.equals(OrderStatus.CANCELLED.name())){
                order.setOrderStatus(newStatus);
            }
            else {
                isValidTransition=false;
            }
        }
        if (currentStatus.equals(OrderStatus.SHIPPED.name())){
            if (newStatus.equals(OrderStatus.DELIVERED.name()) || newStatus.equals(OrderStatus.CANCELLED.name())){
                order.setOrderStatus(newStatus);
            }
            else {
                isValidTransition=false;
            }
        }
        if (currentStatus.equals(OrderStatus.DELIVERED.name())){
            isValidTransition=false;
        }
        if (currentStatus.equals(OrderStatus.CANCELLED.name())){
            isValidTransition=false;
        }
        if(!isValidTransition){
            throw new InvalidOrderStatusTransitionException("Cannot Change order status from "+currentStatus+" to "+newStatus);
        }
        CustomerOrder saved=orderRepository.save(order);
        OrderUpdatedEvent event=new OrderUpdatedEvent(saved.getOrderId(),saved.getOrderStatus());
        orderEventProducer.sendOrderStatusUpdate(event);
        return mapToResponse(saved);
    }

    public OrderResponseDTO cancelOrder(Integer id){
        CustomerOrder order=orderRepository.findById(id).orElseThrow(()->new OrderNotFoundException("Order with id "+id+" not found"));
        OrderStatusUpdateDTO orderStatusUpdateDTO=new OrderStatusUpdateDTO();
        orderStatusUpdateDTO.setOrderStatus(OrderStatus.CANCELLED.name());
        OrderCancelledEvent orderCancelledEvent=new OrderCancelledEvent();
        orderCancelledEvent.setOrderId(id);
        orderCancelledEvent.setProductId(order.getProductId());
        orderCancelledEvent.setOrderQuantity(order.getOrderQuantity());
        orderEventProducer.sendCancelledOrder(orderCancelledEvent);
        return updateOrderStatus(id,orderStatusUpdateDTO);
    }
}
