package com.example.tracking_service.repository;

import com.example.tracking_service.entity.Tracking;
import com.example.tracking_service.entity.TrackingHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TrackingHistoryRepository extends JpaRepository<TrackingHistory, Integer> {
    @Query("""
            SELECT t
            FROM TrackingHistory t
            WHERE t.orderId= :orderId
            ORDER BY t.timeStamp DESC
            """)
    List<TrackingHistory> findTrackingHistory(@Param("orderId") Integer orderId);
}
