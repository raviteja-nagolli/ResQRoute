package com.example.notification_service.controller;

import com.example.notification_service.dto.NotificationCreationDTO;
import com.example.notification_service.dto.NotificationResponseDTO;
import com.example.notification_service.dto.NotificationStatusUpdateDTO;
import com.example.notification_service.service.NotificationDeliveryService;
import com.example.notification_service.service.NotificationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("notification")
public class NotificationController {
    private final NotificationService notificationService;
    private final NotificationDeliveryService notificationDeliveryService;

    public NotificationController(NotificationService notificationService, NotificationDeliveryService notificationDeliveryService) {
        this.notificationService = notificationService;
        this.notificationDeliveryService = notificationDeliveryService;
    }

    @PostMapping
    public NotificationResponseDTO createNotification(@RequestBody NotificationCreationDTO notificationCreationDTO){
        return notificationService.createNotification(notificationCreationDTO);
    }

    @GetMapping
    public List<NotificationResponseDTO> getAllNotification(){
        return notificationService.getAllNotifications();
    }

    @GetMapping("/orderId/{orderId}")
    public List<NotificationResponseDTO> getNotificationsByOrderId(@PathVariable Integer orderId){
        return notificationService.getNotificationsByOrderId(orderId);
    }

    @GetMapping("/{id}")
    public NotificationResponseDTO getNotification(@PathVariable Integer id){
        return notificationService.getNotification(id);
    }

    @PutMapping("/{id}")
    public NotificationResponseDTO updateNotificationStatus(@PathVariable Integer id, @RequestBody NotificationStatusUpdateDTO notificationStatusUpdateDTO){
        return notificationService.updateNotificationStatus(id, notificationStatusUpdateDTO);
    }

    @PostMapping("/retry")
    public String retryNotificationDelivery(){
        return notificationDeliveryService.retryNotificationDelivery();
    }
}
