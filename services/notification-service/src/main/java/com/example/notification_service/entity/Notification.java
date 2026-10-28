package com.example.notification_service.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_notification_order_type",
                        columnNames = {"order_id","notification_type"}
                )
        }
)
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer notificationId;

    private Integer orderId;

    private String notificationType;

    private String notificationMessage;

    private String notificationStatus;

    private Integer retryCount;

    private LocalDateTime notificationCreatedAt;

    private LocalDateTime notificationSentAt;

    public Notification() {
    }

    public Notification(Integer notificationId, Integer orderId, String notificationType, String notificationMessage, String notificationStatus, Integer retryCount, LocalDateTime notificationCreatedAt, LocalDateTime notificationSentAt) {
        this.notificationId = notificationId;
        this.orderId = orderId;
        this.notificationType = notificationType;
        this.notificationMessage = notificationMessage;
        this.notificationStatus = notificationStatus;
        this.retryCount = retryCount;
        this.notificationCreatedAt = notificationCreatedAt;
        this.notificationSentAt = notificationSentAt;
    }

    public Integer getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(Integer notificationId) {
        this.notificationId = notificationId;
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

    public String getNotificationStatus() {
        return notificationStatus;
    }

    public void setNotificationStatus(String notificationStatus) {
        this.notificationStatus = notificationStatus;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    public LocalDateTime getNotificationCreatedAt() {
        return notificationCreatedAt;
    }

    public void setNotificationCreatedAt(LocalDateTime notificationCreatedAt) {
        this.notificationCreatedAt = notificationCreatedAt;
    }

    public LocalDateTime getNotificationSentAt() {
        return notificationSentAt;
    }

    public void setNotificationSentAt(LocalDateTime notificationSentAt) {
        this.notificationSentAt = notificationSentAt;
    }
}
