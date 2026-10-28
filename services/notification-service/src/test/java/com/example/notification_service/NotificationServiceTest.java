package com.example.notification_service;

import com.example.notification_service.dto.NotificationCreationDTO;
import com.example.notification_service.dto.NotificationResponseDTO;
import com.example.notification_service.dto.NotificationStatusUpdateDTO;
import com.example.notification_service.entity.Notification;
import com.example.notification_service.enums.NotificationStatus;
import com.example.notification_service.repository.NotificationRepository;
import com.example.notification_service.service.NotificationDeliveryService;
import com.example.notification_service.service.NotificationService;
import com.example.notification_service.service.NotificationStatusService;
import org.aspectj.weaver.ast.Not;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {
    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationDeliveryService notificationDeliveryService;

    @Mock
    private NotificationStatusService notificationStatusService;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    void newNotificationCreation(){
        NotificationCreationDTO notificationCreationDTO=new NotificationCreationDTO(100,"PLACED","Your order has been placed");
        when(notificationRepository.existsByOrderIdAndNotificationType(notificationCreationDTO.getOrderId(),notificationCreationDTO.getNotificationType())).thenReturn(false);
        Notification notification=new Notification();
        notification.setNotificationId(1);
        notification.setOrderId(notificationCreationDTO.getOrderId());
        notification.setNotificationType(notificationCreationDTO.getNotificationType());
        notification.setNotificationMessage(notificationCreationDTO.getNotificationMessage());
        notification.setNotificationStatus("PENDING");
        notification.setRetryCount(0);
        notification.setNotificationCreatedAt(LocalDateTime.now());
        notification.setNotificationSentAt(null);
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);
        NotificationResponseDTO notificationResponseDTO=notificationService.createNotification(notificationCreationDTO);
        verify(notificationDeliveryService,times(1)).notificationDeliverySimulation(notification.getOrderId(), notification.getNotificationType());
        assertEquals(1,notificationResponseDTO.getNotificationId());
        assertEquals(100,notificationResponseDTO.getOrderId());
        assertEquals("PLACED",notificationResponseDTO.getNotificationType());
        assertEquals("Your order has been placed",notificationResponseDTO.getNotificationMessage());
        assertEquals("PENDING",notificationResponseDTO.getNotificationStatus());
        assertEquals(0,notificationResponseDTO.getRetryCount());
        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void duplicateNotificationShouldNotSaveAndCallDeliverySimulation(){
        NotificationCreationDTO notificationCreationDTO=new NotificationCreationDTO(100,"PLACED","Your order has been placed");
        when(notificationRepository.existsByOrderIdAndNotificationType(notificationCreationDTO.getOrderId(),notificationCreationDTO.getNotificationType())).thenReturn(true);
        Notification notification=new Notification();
        notification.setNotificationId(1);
        notification.setOrderId(notificationCreationDTO.getOrderId());
        notification.setNotificationType(notificationCreationDTO.getNotificationType());
        notification.setNotificationMessage(notificationCreationDTO.getNotificationMessage());
        notification.setNotificationStatus("PENDING");
        notification.setRetryCount(0);
        notification.setNotificationCreatedAt(LocalDateTime.now());
        notification.setNotificationSentAt(null);
        when(notificationRepository.findNotificationByOrderIdAndNotificationType(notificationCreationDTO.getOrderId(),notificationCreationDTO.getNotificationType())).thenReturn(Optional.of(notification));
        NotificationResponseDTO notificationResponseDTO=notificationService.createNotification(notificationCreationDTO);
        assertEquals(1,notificationResponseDTO.getNotificationId());
        verify(notificationRepository,never()).save(any(Notification.class));
        verify(notificationDeliveryService,never()).notificationDeliverySimulation(100,"PLACED");
    }

    @Test
    void getNotificationShouldReturnNotificationWhenFound(){
        Notification notification=new Notification();
        notification.setNotificationId(2);
        notification.setOrderId(101);
        notification.setNotificationType("PLACED");
        notification.setNotificationStatus("PENDING");
        notification.setNotificationMessage("Your order has been placed");
        notification.setRetryCount(0);
        when(notificationRepository.findById(2)).thenReturn(Optional.of(notification));
        NotificationResponseDTO notificationResponseDTO=notificationService.getNotification(2);
        assertEquals(2,notificationResponseDTO.getNotificationId());
        assertEquals(101,notificationResponseDTO.getOrderId());
        assertEquals("PLACED",notificationResponseDTO.getNotificationType());
        assertEquals("PENDING",notificationResponseDTO.getNotificationStatus());
    }

    @Test
    void getNotificationShouldNotReturnNotificationWhenNotFound(){
        when(notificationRepository.findById(999)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class,()->notificationService.getNotification(999));
    }

    @Test
    void getNotificationsByOrderIdShouldReturnNotifications(){
        Notification notification=new Notification();
        notification.setNotificationId(3);
        notification.setOrderId(102);
        notification.setNotificationType("PLACED");
        notification.setNotificationStatus("PENDING");
        notification.setNotificationMessage("Your order has been placed");
        Notification notification1=new Notification();
        notification1.setNotificationId(4);
        notification1.setOrderId(102);
        notification1.setNotificationType("CONFIRMED");
        notification1.setNotificationStatus("PENDING");
        notification1.setNotificationMessage("Your order has been confirmed");
        when(notificationRepository.findNotificationsByOrderId(102)).thenReturn(List.of(notification,notification1));
        List<NotificationResponseDTO> notifications=notificationService.getNotificationsByOrderId(102);
        assertEquals(3,notifications.get(0).getNotificationId());
        assertEquals(102,notifications.get(0).getOrderId());
        assertEquals("PLACED",notifications.get(0).getNotificationType());
        assertEquals("PENDING",notifications.get(0).getNotificationStatus());
        assertEquals("Your order has been placed",notifications.get(0).getNotificationMessage());
        assertEquals(4,notifications.get(1).getNotificationId());
        assertEquals(102,notifications.get(1).getOrderId());
        assertEquals("CONFIRMED",notifications.get(1).getNotificationType());
        assertEquals("PENDING",notifications.get(1).getNotificationStatus());
        assertEquals("Your order has been confirmed",notifications.get(1).getNotificationMessage());
        assertEquals(2,notifications.size());
        verify(notificationRepository).findNotificationsByOrderId(102);
    }

    @Test
    void getNotificationsByOrderIdShouldReturnEmptyList(){
        when(notificationRepository.findNotificationsByOrderId(103)).thenReturn(List.of());
        List<NotificationResponseDTO> notifications=notificationService.getNotificationsByOrderId(103);
//        assertEquals(0,notifications.size());
        assertTrue(notifications.isEmpty());
        verify(notificationRepository).findNotificationsByOrderId(103);
    }

    @Test
    void getAllNotificationsShouldReturnNotifications(){
        Notification notification=new Notification();
        notification.setNotificationId(5);
        notification.setOrderId(104);
        notification.setNotificationType("PLACED");
        notification.setNotificationStatus("PENDING");
        notification.setNotificationMessage("Your order has been placed");
        Notification notification1=new Notification();
        notification1.setNotificationId(6);
        notification1.setOrderId(105);
        notification1.setNotificationType("PLACED");
        notification1.setNotificationStatus("PENDING");
        notification1.setNotificationMessage("Your order has been placed");
        when(notificationRepository.findAll()).thenReturn(List.of(notification,notification1));
        List<NotificationResponseDTO> notifications=notificationService.getAllNotifications();
        assertEquals(2,notifications.size());
        assertEquals(5,notifications.get(0).getNotificationId());
        assertEquals(104,notifications.get(0).getOrderId());
        assertEquals("PLACED",notifications.get(0).getNotificationType());
        assertEquals("PENDING",notifications.get(0).getNotificationStatus());
        assertEquals(6,notifications.get(1).getNotificationId());
        assertEquals(105,notifications.get(1).getOrderId());
        assertEquals("PLACED",notifications.get(1).getNotificationType());
        assertEquals("PENDING",notifications.get(1).getNotificationStatus());
        verify(notificationRepository).findAll();
    }

    @Test
    void getAllNotificationsShouldReturnEmptyList(){
        when(notificationRepository.findAll()).thenReturn(List.of());
        List<NotificationResponseDTO> notifications=notificationService.getAllNotifications();
        assertTrue(notifications.isEmpty());
        verify(notificationRepository).findAll();
    }

    @Test
    void updateNotificationStatusShouldChangePendingToSent(){
        Notification notification=new Notification();
        notification.setNotificationId(10);
        notification.setOrderId(200);
        notification.setNotificationType("PLACED");
        notification.setNotificationStatus("PENDING");
        notification.setRetryCount(0);
        notification.setNotificationMessage("Your order has been placed");
        notification.setNotificationCreatedAt(LocalDateTime.now());
        when(notificationRepository.findById(10)).thenReturn(Optional.of(notification));
        when(notificationStatusService.isValidTransition("PENDING","SENT")).thenReturn(true);
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);
        NotificationStatusUpdateDTO notificationStatusUpdateDTO=new NotificationStatusUpdateDTO("SENT");
        NotificationResponseDTO responseDTO=notificationService.updateNotificationStatus(10,notificationStatusUpdateDTO);
        assertEquals(10,responseDTO.getNotificationId());
        assertEquals(200,responseDTO.getOrderId());
        assertEquals("PLACED",responseDTO.getNotificationType());
        assertEquals("SENT",responseDTO.getNotificationStatus());
        assertEquals(0,responseDTO.getRetryCount());
        assertEquals(notification.getNotificationSentAt(),responseDTO.getNotificationSentAt());
        assertNotNull(responseDTO.getNotificationSentAt());
        verify(notificationRepository).findById(10);
        verify(notificationStatusService).isValidTransition("PENDING","SENT");
        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void updateNotificationStatusShouldChangePendingToFailed(){
        Notification notification=new Notification();
        notification.setNotificationId(11);
        notification.setOrderId(201);
        notification.setNotificationType("PLACED");
        notification.setNotificationStatus("PENDING");
        notification.setRetryCount(0);
        notification.setNotificationMessage("Your order has been placed");
        notification.setNotificationCreatedAt(LocalDateTime.now());
        when(notificationRepository.findById(11)).thenReturn(Optional.of(notification));
        when(notificationStatusService.isValidTransition("PENDING","FAILED")).thenReturn(true);
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);
        NotificationStatusUpdateDTO notificationStatusUpdateDTO=new NotificationStatusUpdateDTO("FAILED");
        NotificationResponseDTO responseDTO=notificationService.updateNotificationStatus(11,notificationStatusUpdateDTO);
        assertEquals(11,responseDTO.getNotificationId());
        assertEquals(201,responseDTO.getOrderId());
        assertEquals("PLACED",responseDTO.getNotificationType());
        assertEquals("FAILED",responseDTO.getNotificationStatus());
        assertEquals(0,responseDTO.getRetryCount());
        assertNull(responseDTO.getNotificationSentAt());
        verify(notificationRepository).findById(11);
        verify(notificationStatusService).isValidTransition("PENDING","FAILED");
        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void updateNotificationStatusShouldChangeSentToRead(){
        Notification notification=new Notification();
        notification.setNotificationId(12);
        notification.setOrderId(202);
        notification.setNotificationType("PLACED");
        notification.setNotificationStatus("SENT");
        notification.setNotificationMessage("Your order has been placed");
        notification.setRetryCount(0);
        notification.setNotificationCreatedAt(LocalDateTime.now());
        notification.setNotificationSentAt(LocalDateTime.now());
        when(notificationRepository.findById(12)).thenReturn(Optional.of(notification));
        when(notificationStatusService.isValidTransition("SENT","READ")).thenReturn(true);
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);
        NotificationStatusUpdateDTO notificationStatusUpdateDTO=new NotificationStatusUpdateDTO("READ");
        NotificationResponseDTO notificationResponseDTO=notificationService.updateNotificationStatus(12,notificationStatusUpdateDTO);
        assertEquals(12,notificationResponseDTO.getNotificationId());
        assertEquals(202,notificationResponseDTO.getOrderId());
        assertEquals("PLACED",notificationResponseDTO.getNotificationType());
        assertEquals("READ",notificationResponseDTO.getNotificationStatus());
        assertEquals(0,notificationResponseDTO.getRetryCount());
        assertEquals(notification.getNotificationSentAt(),notificationResponseDTO.getNotificationSentAt());
        verify(notificationRepository).findById(12);
        verify(notificationStatusService).isValidTransition("SENT","READ");
        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void updateNotificationStatusShouldRejectInvalidTransition(){
        Notification notification=new Notification();
        notification.setNotificationId(13);
        notification.setOrderId(203);
        notification.setNotificationType("PLACED");
        notification.setNotificationStatus("READ");
        notification.setNotificationMessage("Your order has been placed");
        notification.setNotificationCreatedAt(LocalDateTime.now());
        notification.setNotificationSentAt(LocalDateTime.now());
        when(notificationRepository.findById(13)).thenReturn(Optional.of(notification));
        when(notificationStatusService.isValidTransition("READ","SENT")).thenReturn(false);
        NotificationStatusUpdateDTO notificationStatusUpdateDTO=new NotificationStatusUpdateDTO("SENT");
        RuntimeException exception=assertThrows(RuntimeException.class,()->notificationService.updateNotificationStatus(13,notificationStatusUpdateDTO));
        assertEquals("Cannot change notification status from READ to SENT",exception.getMessage());
        verify(notificationStatusService).isValidTransition("READ","SENT");
        verify(notificationRepository).findById(13);
        verify(notificationRepository,never()).save(any(Notification.class));
    }

    @Test
    void updateNotificationStatusShouldRejectWhenMaxRetriesAreReached(){
        Notification notification=new Notification();
        notification.setNotificationId(14);
        notification.setOrderId(204);
        notification.setNotificationType("PLACED");
        notification.setNotificationStatus("FAILED");
        notification.setNotificationMessage("Your order has been placed");
        notification.setRetryCount(3);
        notification.setNotificationCreatedAt(LocalDateTime.now());
        notification.setNotificationSentAt(LocalDateTime.now());
        when(notificationRepository.findById(14)).thenReturn(Optional.of(notification));
        when(notificationStatusService.isValidTransition("FAILED","PENDING")).thenReturn(true);
        NotificationStatusUpdateDTO notificationStatusUpdateDTO=new NotificationStatusUpdateDTO("PENDING");
        RuntimeException exception=assertThrows(RuntimeException.class,()->notificationService.updateNotificationStatus(14,notificationStatusUpdateDTO));
        assertEquals("Retry limit exceeded!",exception.getMessage());
        verify(notificationRepository).findById(14);
        verify(notificationStatusService).isValidTransition("FAILED","PENDING");
        verify(notificationRepository,never()).save(any(Notification.class));
    }

    @Test
    void updateNotificationStatusShouldRetryFailedNotification(){
        Notification notification=new Notification();
        notification.setNotificationId(15);
        notification.setOrderId(205);
        notification.setNotificationType("PLACED");
        notification.setNotificationStatus("FAILED");
        notification.setNotificationMessage("Your order has been placed");
        notification.setRetryCount(1);
        notification.setNotificationCreatedAt(LocalDateTime.now());
        when(notificationRepository.findById(15)).thenReturn(Optional.of(notification));
        when(notificationStatusService.isValidTransition("FAILED","PENDING")).thenReturn(true);
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);
        NotificationStatusUpdateDTO notificationStatusUpdateDTO=new NotificationStatusUpdateDTO("PENDING");
        NotificationResponseDTO responseDTO=notificationService.updateNotificationStatus(15,notificationStatusUpdateDTO);
        assertEquals(15,responseDTO.getNotificationId());
        assertEquals(205,responseDTO.getOrderId());
        assertEquals("PLACED",responseDTO.getNotificationType());
        assertEquals("PENDING",responseDTO.getNotificationStatus());
        assertEquals("Your order has been placed",responseDTO.getNotificationMessage());
        assertEquals(2,responseDTO.getRetryCount());
        assertEquals(2,notification.getRetryCount());
        verify(notificationRepository).findById(15);
        verify(notificationStatusService).isValidTransition("FAILED","PENDING");
        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void updateNotificationStatusShouldRetryFailedNotificationWithFinalRetry(){
        Notification notification=new Notification();
        notification.setNotificationId(15);
        notification.setOrderId(205);
        notification.setNotificationType("PLACED");
        notification.setNotificationStatus("FAILED");
        notification.setNotificationMessage("Your order has been placed");
        notification.setRetryCount(2);
        notification.setNotificationCreatedAt(LocalDateTime.now());
        when(notificationRepository.findById(15)).thenReturn(Optional.of(notification));
        when(notificationStatusService.isValidTransition("FAILED","PENDING")).thenReturn(true);
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);
        NotificationStatusUpdateDTO notificationStatusUpdateDTO=new NotificationStatusUpdateDTO("PENDING");
        NotificationResponseDTO responseDTO=notificationService.updateNotificationStatus(15,notificationStatusUpdateDTO);
        assertEquals(15,responseDTO.getNotificationId());
        assertEquals(205,responseDTO.getOrderId());
        assertEquals("PLACED",responseDTO.getNotificationType());
        assertEquals("PENDING",responseDTO.getNotificationStatus());
        assertEquals("Your order has been placed",responseDTO.getNotificationMessage());
        assertEquals(3,responseDTO.getRetryCount());
        assertEquals(3,notification.getRetryCount());
        verify(notificationRepository).findById(15);
        verify(notificationStatusService).isValidTransition("FAILED","PENDING");
        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void updateNotificationHandlingAttemptToUpdateNotExistingNotification(){
        when(notificationRepository.findById(16)).thenReturn(Optional.empty());
        NotificationStatusUpdateDTO notificationStatusUpdateDTO=new NotificationStatusUpdateDTO("SENT");
        assertThrows(NoSuchElementException.class,()->notificationService.updateNotificationStatus(16,notificationStatusUpdateDTO));
        verify(notificationRepository).findById(16);
        verify(notificationStatusService,never()).isValidTransition(any(String.class),any(String.class));
        verify(notificationRepository,never()).save(any(Notification.class));
    }
}
