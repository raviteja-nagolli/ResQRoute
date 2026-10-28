package com.example.notification_service.dto;

public class NotificationCreationDTO {
    private Integer orderId;
    private String notificationType;
    private String notificationMessage;

    public NotificationCreationDTO() {
    }

    public NotificationCreationDTO(Integer orderId, String notificationType, String notificationMessage) {
        this.orderId = orderId;
        this.notificationType = notificationType;
        this.notificationMessage = notificationMessage;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public String getNotificationMessage() {
        return notificationMessage;
    }

    public void setNotificationMessage(String notificationMessage) {
        this.notificationMessage = notificationMessage;
    }
}
