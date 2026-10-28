package com.example.tracking_service.controller;

import com.example.tracking_service.dto.TrackingCreationDTO;
import com.example.tracking_service.dto.TrackingHistoryResponseDTO;
import com.example.tracking_service.dto.TrackingResponseDto;
import com.example.tracking_service.service.TrackingService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("tracking")
public class TrackingController {
    private final TrackingService trackingService;

    public TrackingController(TrackingService trackingService) {
        this.trackingService = trackingService;
    }

    @PostMapping
    public TrackingResponseDto createNewTracking(TrackingCreationDTO trackingCreationDTO){
        return trackingService.createNewTracking(trackingCreationDTO);
    }

    @GetMapping("/{id}")
    public TrackingResponseDto getTrackingById(@PathVariable Integer id){
        return trackingService.getTrackingById(id);
    }

    @GetMapping
    public List<TrackingResponseDto> getAllTracking(){
        return trackingService.getAllTracking();
    }

    @GetMapping("/history/{id}")
    public List<TrackingHistoryResponseDTO> getTrackingHistory(@PathVariable Integer id){
        return trackingService.getTrackingHistory(id);
    }
}
