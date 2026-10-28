package com.example.api_gateway.repository;

import com.example.api_gateway.entity.UserRegisterDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRegisterDetailsRepository extends JpaRepository<UserRegisterDetails,Integer> {
    Optional<UserRegisterDetails> findByUserName(String userName);
}
