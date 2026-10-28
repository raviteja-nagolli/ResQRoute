package com.example.inventory_service.repository;

import com.example.inventory_service.entity.CancelledOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CancelledOrderRepository extends JpaRepository<CancelledOrder,Integer> {
    public boolean existsByOrderId(Integer id);
}
