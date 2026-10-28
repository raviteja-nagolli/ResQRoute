package com.example.inventory_service.service;

import com.example.inventory_service.dto.InventoryRequestDTO;
import com.example.inventory_service.dto.InventoryResponseDTO;
import com.example.inventory_service.entity.CancelledOrder;
import com.example.inventory_service.entity.Inventory;
import com.example.inventory_service.entity.ProcessedOrder;
import com.example.inventory_service.event.InventoryFailedEvent;
import com.example.inventory_service.event.OrderCancelledEvent;
import com.example.inventory_service.event.OrderCreatedEvent;
import com.example.inventory_service.kafka.InventoryEventProducer;
import com.example.inventory_service.event.InventoryReservedEvent;
import com.example.inventory_service.repository.CancelledOrderRepository;
import com.example.inventory_service.repository.InventoryRepository;
import com.example.inventory_service.repository.ProcessedOrderRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryService {
    private static final Logger log = LoggerFactory.getLogger(InventoryService.class);
    private final InventoryRepository inventoryRepository;
    private final ProcessedOrderRepository processedOrderRepository;
    private final InventoryEventProducer inventoryEventProducer;
    private final CancelledOrderRepository cancelledOrderRepository;

    public InventoryService(InventoryRepository inventoryRepository, ProcessedOrderRepository processedOrderRepository, InventoryEventProducer inventoryEventProducer, CancelledOrderRepository cancelledOrderRepository) {
        this.inventoryRepository = inventoryRepository;
        this.processedOrderRepository = processedOrderRepository;
        this.inventoryEventProducer = inventoryEventProducer;
        this.cancelledOrderRepository = cancelledOrderRepository;
    }

    public InventoryResponseDTO mapToResponse(Inventory inventory){
        InventoryResponseDTO responseDTO=new InventoryResponseDTO();
        responseDTO.setInventoryId(inventory.getInventoryId());
        responseDTO.setProductId(inventory.getProductId());
        responseDTO.setAvailableQuantity(inventory.getAvailableQuantity());
        return responseDTO;
    }

    public InventoryResponseDTO addQuantityOfProduct(InventoryRequestDTO inventoryRequestDTO){
        Inventory inventory=new Inventory();
        inventory.setProductId(inventoryRequestDTO.getProductId());
        inventory.setAvailableQuantity(inventoryRequestDTO.getAvailableQuantity());
        Inventory saved=inventoryRepository.save(inventory);
        return mapToResponse(saved);
    }

    public List<InventoryResponseDTO> getQuantitiesOfProducts(){
        return inventoryRepository.findAll().stream().map(this::mapToResponse).toList();
    }

    @Transactional
    public void updateInventoryOnBasisOfOrder(OrderCreatedEvent event){
        if(processedOrderRepository.existsByOrderId(event.getOrderId())){
            log.warn("Order already processed from reserving function: orderId={}. Ignoring duplicate event.",event.getOrderId());
            System.out.println("Order "+event.getOrderId()+" has already been processed. Ignoring duplicate event.");
            return;
        }
        Inventory inventory=inventoryRepository.findByProductId(event.getProductId()).orElseThrow();
        if(inventory.getAvailableQuantity() >= event.getOrderQuantity()) {
            log.info("Reserving inventory: productId={}, orderQuantity={}",event.getProductId(),event.getOrderQuantity());
            inventory.setAvailableQuantity(inventory.getAvailableQuantity() - event.getOrderQuantity());
            inventoryRepository.save(inventory);
            ProcessedOrder processedOrder=new ProcessedOrder();
            processedOrder.setOrderId(event.getOrderId());
            processedOrderRepository.save(processedOrder);
            log.info("Inventory reserved: orderId={}",event.getOrderId());

//            System.out.println("Order "+event.getOrderId()+" processed successfully.");
//            System.out.println("Product "+event.getProductId()+" inventory reduced by "+event.getOrderQuantity());
//            System.out.println("Remaining inventory:"+inventory.getAvailableQuantity());
            InventoryReservedEvent inventoryReservedEvent=new InventoryReservedEvent();
            inventoryReservedEvent.setOrderId(event.getOrderId());
            inventoryReservedEvent.setProductId(event.getProductId());
            log.info("Publishing Inventory reserved Event: orderId={}",event.getOrderId());
            inventoryEventProducer.sendInventoryReservedEvent(inventoryReservedEvent);
        }else {
            //System.out.println("Order "+event.getOrderId()+" cannot be processed.");
            //System.out.println("Insufficient inventory for product "+event.getProductId());
            //System.out.println("Available:"+inventory.getAvailableQuantity()+", Requested:"+event.getOrderQuantity());

            log.warn("Insufficient inventory: orderId={}",event.getOrderId());
            log.debug("Available:{}, Requested:{}",inventory.getAvailableQuantity(),event.getOrderQuantity());
            InventoryFailedEvent inventoryFailedEvent=new InventoryFailedEvent();
            inventoryFailedEvent.setOrderId(event.getOrderId());
            inventoryFailedEvent.setProductId(event.getProductId());
            inventoryFailedEvent.setOrderQuantity(event.getOrderQuantity());
            inventoryFailedEvent.setReason("Insufficient inventory");
            log.info("Publishing Inventory Failed event: orderId={}",event.getOrderId());
            inventoryEventProducer.sendInventoryFailedEvent(inventoryFailedEvent);
        }
    }

    public void cancelOrder(OrderCancelledEvent event){
        if(cancelledOrderRepository.existsByOrderId(event.getOrderId())){
            log.warn("Order already processed from cancelling function: orderId={}. Ignoring duplicate event.",event.getOrderId());
            return;
        }
        log.info("Releasing inventory: orderId={}",event.getOrderId());
        Inventory inventory=inventoryRepository.findByProductId(event.getProductId()).orElseThrow();
        Integer currentQuantity=inventory.getAvailableQuantity();
        Integer updatedQuantity=currentQuantity+ event.getOrderQuantity();
        inventory.setAvailableQuantity(updatedQuantity);
        Inventory saved=inventoryRepository.save(inventory);
        CancelledOrder cancelledOrder=new CancelledOrder();
        cancelledOrder.setOrderId(event.getOrderId());
        cancelledOrder.setProductId(event.getProductId());
        cancelledOrderRepository.save(cancelledOrder);
        log.info("Inventory released: orderId={}",event.getOrderId());
        log.debug("Product quantity of before release:{}",currentQuantity);
        log.debug("Order quantity:{}",event.getOrderQuantity());
        log.debug("Updated quantity after release:{}",saved.getAvailableQuantity());
    }
}
