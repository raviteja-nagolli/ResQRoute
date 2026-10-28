package com.example.api_gateway.service;

import com.example.api_gateway.dto.*;
import com.example.api_gateway.entity.UserRegisterDetails;
import com.example.api_gateway.exception.InvalidLoginCredentialsException;
import com.example.api_gateway.repository.UserRegisterDetailsRepository;
import com.example.api_gateway.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@org.springframework.stereotype.Service
public class Service {
    private final UserRegisterDetailsRepository userRegisterDetailsRepository;
    private final CustomUserDetailsService customUserDetailsService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public Service(UserRegisterDetailsRepository userRegisterDetailsRepository, CustomUserDetailsService customUserDetailsService, BCryptPasswordEncoder bCryptPasswordEncoder, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userRegisterDetailsRepository = userRegisterDetailsRepository;
        this.customUserDetailsService = customUserDetailsService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public String newUser(RegisterRequest request){
        UserRegisterDetails userRegisterDetails=new UserRegisterDetails();
        userRegisterDetails.setUserName(request.getUserName());
        userRegisterDetails.setUserEmail(request.getUserEmail());
        String encodedPassword=bCryptPasswordEncoder.encode(request.getUserPassword());
        userRegisterDetails.setUserPassword(encodedPassword);
        userRegisterDetails.setUserRole(request.getUserRole());
        userRegisterDetailsRepository.save(userRegisterDetails);
        return "User Registered";
    }

    public LoginResponse userLogin(LoginRequest loginRequest){
        try {
            Authentication authentication=authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUserName(),loginRequest.getUserPassword()));
            UserDetails userDetails=(UserDetails) authentication.getPrincipal();
            String accessToken= jwtService.generateAccessToken(userDetails);
            String refreshToken= jwtService.generateRefreshToken(userDetails);
            LoginResponse loginResponse=new LoginResponse();
            loginResponse.setAccessToken(accessToken);
            loginResponse.setRefreshToken(refreshToken);
            return loginResponse;
        }catch (BadCredentialsException exception){
            throw new InvalidLoginCredentialsException("Invalid Username or Password");
        }
    }

    public RefreshResponse refreshRequest(RefreshRequest request){
        String refreshToken=request.getRefreshToken();
        String username= jwtService.extractUsername(refreshToken);
        UserDetails userDetails= customUserDetailsService.loadUserByUsername(username);
        if(!jwtService.validateRefreshToken(refreshToken,userDetails)){
            throw new InvalidLoginCredentialsException("Invalid refresh token");
        }
        String accessToken= jwtService.generateAccessToken(userDetails);
        return new RefreshResponse(accessToken);
    }
}
