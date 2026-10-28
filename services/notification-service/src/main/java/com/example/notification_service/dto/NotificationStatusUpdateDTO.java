package com.example.notification_service.dto;

public class NotificationStatusUpdateDTO {
    private String notificationStatus;

    public NotificationStatusUpdateDTO() {
    }

    public NotificationStatusUpdateDTO(String notificationStatus) {
        this.notificationStatus = notificationStatus;
    }

    public String getNotificationStatus() {
        return notificationStatus;
    }

    public void setNotificationStatus(String notificationStatus) {
        this.notificationStatus = notificationStatus;
    }
}
