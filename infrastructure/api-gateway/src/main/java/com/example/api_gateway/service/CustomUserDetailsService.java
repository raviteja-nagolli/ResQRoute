package com.example.api_gateway.service;

import com.example.api_gateway.entity.UserRegisterDetails;
import com.example.api_gateway.repository.UserRegisterDetailsRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRegisterDetailsRepository userRegisterDetailsRepository;

    public CustomUserDetailsService(UserRegisterDetailsRepository userRegisterDetailsRepository) {
        this.userRegisterDetailsRepository = userRegisterDetailsRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String userName){
        UserRegisterDetails userRegisterDetails=userRegisterDetailsRepository.findByUserName(userName).orElseThrow();
        return User.withUsername(userRegisterDetails.getUserName()).password(userRegisterDetails.getUserPassword()).roles(userRegisterDetails.getUserRole()).build();
    }
}
