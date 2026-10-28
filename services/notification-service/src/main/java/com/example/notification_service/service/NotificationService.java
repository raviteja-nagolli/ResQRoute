package com.example.notification_service.service;

import com.example.notification_service.dto.NotificationCreationDTO;
import com.example.notification_service.dto.NotificationResponseDTO;
import com.example.notification_service.dto.NotificationStatusUpdateDTO;
import com.example.notification_service.entity.Notification;
import com.example.notification_service.enums.NotificationStatus;
import com.example.notification_service.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {
    private static final Logger log=LoggerFactory.getLogger(NotificationService.class);
    private final NotificationRepository notificationRepository;
    private final NotificationDeliveryService notificationDeliveryService;
    private final NotificationStatusService notificationStatusService;

    public NotificationService(NotificationRepository notificationRepository, NotificationDeliveryService notificationDeliveryService, NotificationStatusService notificationStatusService) {
        this.notificationRepository = notificationRepository;
        this.notificationDeliveryService = notificationDeliveryService;
        this.notificationStatusService = notificationStatusService;
    }

    private NotificationResponseDTO toResponseDTO(Notification notification){
        NotificationResponseDTO responseDTO=new NotificationResponseDTO();
        responseDTO.setNotificationId(notification.getNotificationId());
        responseDTO.setOrderId(notification.getOrderId());
        responseDTO.setNotificationType(notification.getNotificationType());
        responseDTO.setNotificationMessage(notification.getNotificationMessage());
        responseDTO.setNotificationStatus(notification.getNotificationStatus());
        responseDTO.setRetryCount(notification.getRetryCount());
        responseDTO.setNotificationCreatedAt(notification.getNotificationCreatedAt());
        responseDTO.setNotificationSentAt(notification.getNotificationSentAt());
        return responseDTO;
    }

    public NotificationResponseDTO createNotification(NotificationCreationDTO notificationCreationDTO){
        if(notificationRepository.existsByOrderIdAndNotificationType(notificationCreationDTO.getOrderId(), notificationCreationDTO.getNotificationType())){
            log.info("Duplicate Entry Found: orderID={}, notificationType={}",notificationCreationDTO.getOrderId(), notificationCreationDTO.getNotificationType());
            return toResponseDTO(notificationRepository.findNotificationByOrderIdAndNotificationType(notificationCreationDTO.getOrderId(), notificationCreationDTO.getNotificationType()).orElseThrow());
        }
        Notification notification=new Notification();
        notification.setOrderId(notificationCreationDTO.getOrderId());
        notification.setNotificationType(notificationCreationDTO.getNotificationType());
        notification.setNotificationMessage(notificationCreationDTO.getNotificationMessage());
        notification.setNotificationStatus(NotificationStatus.PENDING.name());
        notification.setRetryCount(0);
        notification.setNotificationCreatedAt(LocalDateTime.now());
        Notification saved=notificationRepository.save(notification);
        log.info("Notification created:orderID={}, notificationType={}, notificationStatus={}",saved.getOrderId(),saved.getNotificationType(),saved.getNotificationStatus());
        notificationDeliveryService.notificationDeliverySimulation(saved.getOrderId(),saved.getNotificationType());
        return toResponseDTO(saved);
    }

    public List<NotificationResponseDTO> getNotificationsByOrderId(Integer orderId){
        return notificationRepository.findNotificationsByOrderId(orderId).stream().map(this::toResponseDTO).toList();
    }

    public List<NotificationResponseDTO> getAllNotifications(){
        return notificationRepository.findAll().stream().map(this::toResponseDTO).toList();
    }

    public NotificationResponseDTO getNotification(Integer id){
        return toResponseDTO(notificationRepository.findById(id).orElseThrow());
    }

    public NotificationResponseDTO updateNotificationStatus(Integer id, NotificationStatusUpdateDTO notificationStatusUpdateDTO){
        Notification notification=notificationRepository.findById(id).orElseThrow();
        String currentStatus=notification.getNotificationStatus();
        String newStatus=notificationStatusUpdateDTO.getNotificationStatus();
        boolean validTransition= notificationStatusService.isValidTransition(currentStatus, newStatus);
        if(!validTransition){
            throw new RuntimeException("Cannot change notification status from "+currentStatus+" to "+newStatus);
        }
        if(currentStatus.equals(NotificationStatus.PENDING.name())){
            if (newStatus.equals(NotificationStatus.SENT.name())){
                notification.setNotificationStatus(notificationStatusUpdateDTO.getNotificationStatus());
                notification.setNotificationSentAt(LocalDateTime.now());
            }
            else if(newStatus.equals(NotificationStatus.FAILED.name())){
                notification.setNotificationStatus(notificationStatusUpdateDTO.getNotificationStatus());
            }
        }
        if(currentStatus.equals(NotificationStatus.SENT.name())){
            if(newStatus.equals(NotificationStatus.READ.name())){
                notification.setNotificationStatus(notificationStatusUpdateDTO.getNotificationStatus());
            }
        }
        if(currentStatus.equals(NotificationStatus.FAILED.name())){
            if (newStatus.equals(NotificationStatus.PENDING.name())){
                if(notification.getRetryCount()<3) {
                    notification.setNotificationStatus(notificationStatusUpdateDTO.getNotificationStatus());
                    notification.setRetryCount(notification.getRetryCount() + 1);
                }
                else{
                    throw new RuntimeException("Retry limit exceeded!");
                }
            }
        }
        Notification saved=notificationRepository.save(notification);
        log.info("Notification Status Updated: notificationId={}, previousStatus={}, updatedStatus={}",id, currentStatus, newStatus);
        return toResponseDTO(saved);
    }
}
