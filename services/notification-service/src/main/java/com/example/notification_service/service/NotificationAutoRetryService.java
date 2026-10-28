package com.example.notification_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class NotificationAutoRetryService {
    private static final Logger log= LoggerFactory.getLogger(NotificationAutoRetryService.class);
    private final NotificationService notificationService;
    private final NotificationStatusService notificationStatusService;
    private final NotificationDeliveryService notificationDeliveryService;

    public NotificationAutoRetryService(NotificationService notificationService, NotificationStatusService notificationStatusService, NotificationDeliveryService notificationDeliveryService) {
        this.notificationService = notificationService;
        this.notificationStatusService = notificationStatusService;
        this.notificationDeliveryService = notificationDeliveryService;
    }

    @Scheduled(fixedDelay = 30000)
    public void retryNotifications(){
        log.info("Initiating Auto Retry");
        notificationDeliveryService.retryNotificationDelivery();
        log.info("Auto retry completed");
    }
}
