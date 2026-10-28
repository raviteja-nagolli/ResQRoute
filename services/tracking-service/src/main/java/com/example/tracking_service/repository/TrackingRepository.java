package com.example.tracking_service.repository;

import com.example.tracking_service.entity.Tracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TrackingRepository extends JpaRepository<Tracking, Integer> {
    Optional<Tracking> findByOrderId(Integer id);
}
