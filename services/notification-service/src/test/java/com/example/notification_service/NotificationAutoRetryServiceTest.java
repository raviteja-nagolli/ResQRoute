package com.example.notification_service;

import com.example.notification_service.service.NotificationAutoRetryService;
import com.example.notification_service.service.NotificationDeliveryService;
import com.example.notification_service.service.NotificationService;
import com.example.notification_service.service.NotificationStatusService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class NotificationAutoRetryServiceTest {
    @Mock
    private NotificationService notificationService;

    @Mock
    private NotificationStatusService notificationStatusService;

    @Mock
    private NotificationDeliveryService notificationDeliveryService;

    @InjectMocks
    private NotificationAutoRetryService notificationAutoRetryService;

    @Test
    void retryNotifications(){
        notificationAutoRetryService.retryNotifications();
        verify(notificationDeliveryService,times(1)).retryNotificationDelivery();
    }

    @Test
    void multipleRetries(){
        notificationAutoRetryService.retryNotifications();
        notificationAutoRetryService.retryNotifications();
        notificationAutoRetryService.retryNotifications();
        verify(notificationDeliveryService,times(3)).retryNotificationDelivery();
    }
}
