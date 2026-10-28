package com.example.tracking_service.service;

import com.example.tracking_service.dto.*;
import com.example.tracking_service.entity.Tracking;
import com.example.tracking_service.entity.TrackingHistory;
import com.example.tracking_service.enums.OrderStatus;
import com.example.tracking_service.event.InventoryReservedEvent;
import com.example.tracking_service.repository.TrackingHistoryRepository;
import com.example.tracking_service.repository.TrackingRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TrackingService {
    private final TrackingRepository trackingRepository;
    private final TrackingHistoryRepository trackingHistoryRepository;

    public TrackingService(TrackingRepository trackingRepository, TrackingHistoryRepository trackingHistoryRepository) {
        this.trackingRepository = trackingRepository;
        this.trackingHistoryRepository = trackingHistoryRepository;
    }

    private TrackingResponseDto toResponseDto(Tracking tracking){
        TrackingResponseDto trackingResponseDto=new TrackingResponseDto();
        trackingResponseDto.setTrackingId(tracking.getTrackingId());
        trackingResponseDto.setOrderId(tracking.getOrderId());
        trackingResponseDto.setOrderStatus(tracking.getOrderStatus());
        trackingResponseDto.setOrderLocation(tracking.getOrderLocation());
        trackingResponseDto.setTrackingUpdatedAt(tracking.getTrackingUpdatedAt());
        return trackingResponseDto;
    }

    private TrackingHistoryResponseDTO toHistoryResponseDto(TrackingHistory trackingHistory){
        TrackingHistoryResponseDTO trackingHistoryResponseDTO=new TrackingHistoryResponseDTO();
        trackingHistoryResponseDTO.setTrackingHistoryId(trackingHistory.getTrackingHistoryId());
        trackingHistoryResponseDTO.setOrderId(trackingHistory.getOrderId());
        trackingHistoryResponseDTO.setOrderStatus(trackingHistory.getOrderStatus());
        trackingHistoryResponseDTO.setOrderLocation(trackingHistory.getOrderLocation());
        trackingHistoryResponseDTO.setTimeStamp(trackingHistory.getTimeStamp());
        return trackingHistoryResponseDTO;
    }

    private TrackingHistory toHistoryCreationDTO(Tracking tracking){
        TrackingHistory trackingHistory=new TrackingHistory();
        trackingHistory.setOrderId(tracking.getOrderId());
        trackingHistory.setOrderLocation(tracking.getOrderLocation());
        trackingHistory.setOrderStatus(tracking.getOrderStatus());
        trackingHistory.setTimeStamp(tracking.getTrackingUpdatedAt());
        return trackingHistory;
    }

    public TrackingResponseDto createNewTracking(TrackingCreationDTO trackingCreationDTO){
        Tracking tracking=new Tracking();
        tracking.setOrderId(trackingCreationDTO.getOrderId());
        tracking.setOrderStatus(OrderStatus.PLACED.name());
        tracking.setOrderLocation("Bengaluru Warehouse");
        tracking.setTrackingUpdatedAt(LocalDateTime.now());
        Tracking saved=trackingRepository.save(tracking);
        TrackingHistory trackingHistory=toHistoryCreationDTO(saved);
        trackingHistoryRepository.save(trackingHistory);
        return toResponseDto(saved);
    }

//    public TrackingResponseDto updateTrackingForIR(TrackingUpdationDto trackingUpdationDto){
//        Tracking tracking=trackingRepository.findByOrderId(trackingUpdationDto.getOrderId()).orElseThrow();
//        tracking.setOrderStatus(OrderStatus.CONFIRMED.name());
//        tracking.setTrackingUpdatedAt(LocalDateTime.now());
//        Tracking saved=trackingRepository.save(tracking);
//        TrackingHistory trackingHistory=toHistoryCreationDTO(saved);
//        trackingHistoryRepository.save(trackingHistory);
//        return toResponseDto(saved);
//    }

    public TrackingResponseDto updateTrackingForCancelled(TrackingUpdationDto trackingUpdationDto){
        Tracking tracking=trackingRepository.findByOrderId(trackingUpdationDto.getOrderId()).orElseThrow();
        tracking.setOrderStatus(OrderStatus.CANCELLED.name());
        tracking.setTrackingUpdatedAt(LocalDateTime.now());
        Tracking saved=trackingRepository.save(tracking);
        TrackingHistory trackingHistory=toHistoryCreationDTO(saved);
        trackingHistoryRepository.save(trackingHistory);
        return toResponseDto(saved);
    }

    public TrackingResponseDto updateTrackingForStatus(TrackingStatusUpdationDTO trackingStatusUpdationDTO){
        Tracking tracking=trackingRepository.findByOrderId(trackingStatusUpdationDTO.getOrderId()).orElseThrow();
        tracking.setOrderStatus(trackingStatusUpdationDTO.getOrderStatus());
        tracking.setTrackingUpdatedAt(LocalDateTime.now());
        Tracking saved=trackingRepository.save(tracking);
        TrackingHistory trackingHistory=toHistoryCreationDTO(saved);
        trackingHistoryRepository.save(trackingHistory);
        return toResponseDto(saved);
    }

    public TrackingResponseDto getTrackingById(Integer id){
        Tracking tracking=trackingRepository.findById(id).orElseThrow();
        return toResponseDto(tracking);
    }

    public List<TrackingResponseDto> getAllTracking(){
        List<Tracking> trackings=trackingRepository.findAll();
        return trackings.stream().map(this::toResponseDto).toList();
    }

    public List<TrackingHistoryResponseDTO> getTrackingHistory(Integer id){
        List<TrackingHistory> trackingHistories=trackingHistoryRepository.findTrackingHistory(id);
        return trackingHistories.stream().map(this::toHistoryResponseDto).toList();
    }
}
