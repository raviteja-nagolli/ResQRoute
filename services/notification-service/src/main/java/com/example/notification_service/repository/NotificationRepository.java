package com.example.notification_service.repository;

import com.example.notification_service.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification,Integer> {
    @Query("""
            SELECT n
            FROM Notification n
            WHERE n.orderId= :orderId
            """)
    List<Notification> findNotificationsByOrderId(@Param("orderId") Integer orderId);

    @Query("""
            SELECT n
            FROM Notification n
            WHERE n.orderId= :orderId
            AND n.notificationType= :notificationType
            """)
    Optional<Notification> findNotificationByOrderIdAndNotificationType(@Param("orderId") Integer orderId, @Param("notificationType") String notificationType);

    boolean existsByOrderIdAndNotificationType(Integer orderId, String notificationType);

    @Query("""
            SELECT n
            FROM Notification n
            WHERE n.notificationStatus= "FAILED"
            """)
    List<Notification> findFailedNotifications();
}
