package com.example.inventory_service.repository;

import com.example.inventory_service.entity.ProcessedOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProcessedOrderRepository extends JpaRepository<ProcessedOrder, Integer> {
    public boolean existsByOrderId(Integer id);
}
