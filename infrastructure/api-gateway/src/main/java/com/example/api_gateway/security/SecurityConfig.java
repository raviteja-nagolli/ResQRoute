package com.example.api_gateway.security;

import com.example.api_gateway.exception.CustomAccessDeniedHandler;
import org.hibernate.Session;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private final JwtFilter jwtFilter;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    public SecurityConfig(JwtFilter jwtFilter, CustomAccessDeniedHandler customAccessDeniedHandler) {
        this.jwtFilter = jwtFilter;
        this.customAccessDeniedHandler = customAccessDeniedHandler;
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration){
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity){
        return httpSecurity.csrf(csrf->csrf.disable())
                .authorizeHttpRequests(auth->auth
                        .requestMatchers("/api/login","/api/register","/api/refresh").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/product/**").authenticated()
                        .requestMatchers(HttpMethod.POST,"/api/product/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,"/api/product/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE,"/api/product/**").hasRole("ADMIN")
                        .requestMatchers("/api/order/**").authenticated()
                        .requestMatchers(HttpMethod.GET,"/api/tracking/**").authenticated()
                        .requestMatchers(HttpMethod.POST,"/api/tracking/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET,"/api/notification/**").authenticated()
                        .requestMatchers(HttpMethod.PUT,"/api/notification/**").hasRole("ADMIN")
                        .anyRequest()
                        .authenticated()
                )
                .exceptionHandling(exception->exception.accessDeniedHandler(customAccessDeniedHandler))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(session-> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }
}
