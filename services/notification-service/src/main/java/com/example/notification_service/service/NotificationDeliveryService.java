package com.example.notification_service.service;

import com.example.notification_service.dto.NotificationResponseDTO;
import com.example.notification_service.entity.Notification;
import com.example.notification_service.enums.NotificationStatus;
import com.example.notification_service.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class NotificationDeliveryService {
    private static final Logger log= LoggerFactory.getLogger(NotificationDeliveryService.class);
    private final NotificationRepository notificationRepository;
    private final NotificationStatusService notificationStatusService;

    public NotificationDeliveryService(NotificationRepository notificationRepository, NotificationStatusService notificationStatusService) {
        this.notificationRepository = notificationRepository;
        this.notificationStatusService = notificationStatusService;
    }

    private void sent(Notification notification){
        if(!notificationStatusService.isValidTransition(notification.getNotificationStatus(),NotificationStatus.SENT.name())){
            return;
        }
        notification.setNotificationStatus(NotificationStatus.SENT.name());
        notification.setNotificationSentAt(LocalDateTime.now());
        Notification saved=notificationRepository.save(notification);
        log.info("Notification delivered: notificationId={}, notificationStatus={}",saved.getNotificationId(),saved.getNotificationStatus());
    }

    private void failed(Notification notification){
        if(!notificationStatusService.isValidTransition(notification.getNotificationStatus(),NotificationStatus.FAILED.name())){
            return;
        }
        notification.setNotificationStatus(NotificationStatus.FAILED.name());
        Notification saved=notificationRepository.save(notification);
        log.info("Notification failed: notificationId={}, notificationStatus={}",saved.getNotificationId(),saved.getNotificationStatus());
    }

    private void pending(Notification notification){
        if(!notificationStatusService.isValidTransition(notification.getNotificationStatus(),NotificationStatus.PENDING.name())){
            return;
        }
        notification.setRetryCount(notification.getRetryCount()+1);
        notification.setNotificationStatus(NotificationStatus.PENDING.name());
        Notification saved=notificationRepository.save(notification);
        log.info("Notification pending: notificationId={}, notificationStatus={}",notification.getNotificationId(),notification.getNotificationStatus());
    }

    public void notificationDeliverySimulation(Integer orderId, String notificationType){
        Notification notification=notificationRepository.findNotificationByOrderIdAndNotificationType(orderId,notificationType).orElseThrow();
        log.info("Attempting delivery: notificationId={}",notification.getNotificationId());
        failed(notification);
        log.info("Notification delivery failed: notificationId={}",notification.getNotificationId());
    }

    public String retryNotificationDelivery(){
        boolean retryLimitExceeded=false;
        List<Notification> failedNotifications=notificationRepository.findFailedNotifications();
        for (Notification notification:failedNotifications){
            if(notification.getRetryCount()>=3){
                retryLimitExceeded=true;
                continue;
            }
            log.info("Attempting delivery retry: notificationId={}",notification.getNotificationId());
            pending(notification);
            log.debug("Notification pending: notificationId={}",notification.getNotificationId());
            sent(notification);
            log.debug("Notification sent: notificationId={}",notification.getNotificationId());
            if(NotificationStatus.SENT.name().equals(notification.getNotificationStatus())) {
                log.info("Notification delivery retry successful: notificationId={}", notification.getNotificationId());
            }
        }
        if(retryLimitExceeded){
            return "Notification retry count exceeded";
        }
        return "Retry successful";
    }
}
