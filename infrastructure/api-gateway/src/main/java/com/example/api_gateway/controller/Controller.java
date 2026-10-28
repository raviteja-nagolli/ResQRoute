package com.example.api_gateway.controller;

import com.example.api_gateway.dto.*;
import com.example.api_gateway.service.Service;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
public class Controller {
    private final Service service;

    public Controller(Service service) {
        this.service = service;
    }

    @PostMapping("/register")
    public String newUser(@Valid @RequestBody RegisterRequest request){
        return service.newUser(request);
    }

    @PostMapping("/login")
    public LoginResponse userLogin(@Valid @RequestBody LoginRequest request){
        return service.userLogin(request);
    }

    @PostMapping("/refresh")
    public RefreshResponse refreshToken(@RequestBody RefreshRequest request){
        return service.refreshRequest(request);
    }
}
