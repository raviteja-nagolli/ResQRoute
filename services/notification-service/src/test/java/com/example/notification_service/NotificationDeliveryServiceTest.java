package com.example.notification_service;

import com.example.notification_service.entity.Notification;
import com.example.notification_service.repository.NotificationRepository;
import com.example.notification_service.service.NotificationDeliveryService;
import com.example.notification_service.service.NotificationStatusService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationDeliveryServiceTest {
    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationStatusService notificationStatusService;

    @InjectMocks
    private NotificationDeliveryService notificationDeliveryService;

    @Test
    void failedNotificationShouldUpdateStatusAndRetryCount(){
        Notification notification=new Notification();
        notification.setNotificationId(1);
        notification.setOrderId(70);
        notification.setNotificationType("PLACED");
        notification.setNotificationStatus("PENDING");
        notification.setRetryCount(0);
        when(notificationRepository.findNotificationByOrderIdAndNotificationType(70,"PLACED")).thenReturn(Optional.of(notification));
        when(notificationStatusService.isValidTransition("PENDING","FAILED")).thenReturn(true);
        when(notificationRepository.save(notification)).thenReturn(notification);
        notificationDeliveryService.notificationDeliverySimulation(70,"PLACED");
        assertEquals("FAILED",notification.getNotificationStatus());
        assertEquals(0,notification.getRetryCount());
        verify(notificationRepository).save(notification);
    }

    @Test
    void invalidTransitionShouldNotUpdateOrSaveNotification(){
        Notification notification=new Notification();
        notification.setNotificationId(2);
        notification.setOrderId(71);
        notification.setNotificationType("PLACED");
        notification.setNotificationStatus("SENT");
        notification.setRetryCount(0);
        when(notificationRepository.findNotificationByOrderIdAndNotificationType(71,"PLACED")).thenReturn(Optional.of(notification));
        when(notificationStatusService.isValidTransition("SENT","FAILED")).thenReturn(false);
        notificationDeliveryService.notificationDeliverySimulation(71,"PLACED");
        assertEquals("SENT",notification.getNotificationStatus());
        assertEquals(0,notification.getRetryCount());
        verify(notificationRepository,never()).save(any(Notification.class));
    }

    @Test
    void retryShouldNotProceedWhenRetryCountReachesMaximum(){
        Notification notification=new Notification();
        notification.setNotificationId(3);
        notification.setOrderId(72);
        notification.setNotificationType("PLACED");
        notification.setNotificationStatus("FAILED");
        notification.setRetryCount(3);
        when(notificationRepository.findFailedNotifications()).thenReturn(List.of(notification));
        String result= notificationDeliveryService.retryNotificationDelivery();
        assertEquals("Notification retry count exceeded",result);
        assertEquals("FAILED",notification.getNotificationStatus());
        assertEquals(3,notification.getRetryCount());
        verify(notificationRepository,never()).save(any(Notification.class));
    }

    @Test
    void retryShouldProceedWhenRetryCountNotMaximum(){
        Notification notification=new Notification();
        notification.setNotificationId(4);
        notification.setOrderId(73);
        notification.setNotificationType("PLACED");
        notification.setNotificationStatus("FAILED");
        notification.setRetryCount(1);
        when(notificationRepository.findFailedNotifications()).thenReturn(List.of(notification));
        when(notificationStatusService.isValidTransition("FAILED","PENDING")).thenReturn(true);
        when(notificationStatusService.isValidTransition("PENDING","SENT")).thenReturn(true);
        when(notificationRepository.save(notification)).thenReturn(notification);
        notificationDeliveryService.retryNotificationDelivery();
        assertEquals("SENT",notification.getNotificationStatus());
        assertEquals(2,notification.getRetryCount());
        verify(notificationRepository,times(2)).save(notification);
    }

    @Test
    void retryShouldProceedWhenRetryCountOfOneNotificationMaximumAndAnotherIsNotMaximum(){
        Notification notification=new Notification();
        notification.setNotificationId(5);
        notification.setOrderId(73);
        notification.setNotificationType("PLACED");
        notification.setNotificationStatus("FAILED");
        notification.setRetryCount(3);
        Notification notification1=new Notification();
        notification1.setNotificationId(6);
        notification1.setOrderId(74);
        notification1.setNotificationType("PLACED");
        notification1.setNotificationStatus("FAILED");
        notification1.setRetryCount(1);
        when(notificationRepository.findFailedNotifications()).thenReturn(List.of(notification,notification1));
        when(notificationStatusService.isValidTransition("FAILED","PENDING")).thenReturn(true);
        when(notificationStatusService.isValidTransition("PENDING","SENT")).thenReturn(true);
        when(notificationRepository.save(notification1)).thenReturn(notification1);
        notificationDeliveryService.retryNotificationDelivery();
        assertEquals("FAILED",notification.getNotificationStatus());
        assertEquals(3,notification.getRetryCount());
        assertEquals("SENT",notification1.getNotificationStatus());
        assertEquals(2,notification1.getRetryCount());
        verify(notificationRepository,times(2)).save(notification1);
    }

    @Test
    void whenNoFailedNotification(){
        when(notificationRepository.findFailedNotifications()).thenReturn(List.of());
        String result=notificationDeliveryService.retryNotificationDelivery();
        assertEquals("Retry successful",result);
        verify(notificationRepository,never()).save(any(Notification.class));
    }

    @Test
    void retryShouldNotProceedWhenPendingTransitionIsInvalid(){
        Notification notification=new Notification();
        notification.setNotificationId(7);
        notification.setOrderId(75);
        notification.setNotificationType("PLACED");
        notification.setNotificationStatus("FAILED");
        notification.setRetryCount(1);
        when(notificationRepository.findFailedNotifications()).thenReturn(List.of(notification));
        when(notificationStatusService.isValidTransition("FAILED","PENDING")).thenReturn(false);
        when(notificationStatusService.isValidTransition("FAILED","SENT")).thenReturn(false);
        notificationDeliveryService.retryNotificationDelivery();
        assertEquals("FAILED",notification.getNotificationStatus());
        assertEquals(1,notification.getRetryCount());
        verify(notificationRepository,never()).save(any(Notification.class));
    }
}
