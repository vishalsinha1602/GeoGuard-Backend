package com.backend.geosentinel.alert.service;

import com.backend.geosentinel.alert.dto.AlertResponseDto;
import com.backend.geosentinel.alert.entity.Alert;
import com.backend.geosentinel.alert.entity.enums.AlertType;
import com.backend.geosentinel.alert.repository.AlertRepository;
import com.backend.geosentinel.devices.entity.Device;
import com.backend.geosentinel.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class AlertServiceImpl implements AlertService {

    private final AlertRepository alertRepository;
    private final ModelMapper modelMapper;

    @Override
    public void createAlert(
            Device device,
            AlertType type,
            String title,
            String message) {

        Alert alert = Alert.builder()
                .device(device)
                .type(type)
                .title(title)
                .message(message)
                .build();

        alertRepository.save(alert);
    }

    @Override
    public List<AlertResponseDto> getAlerts(
            UUID devicePublicId) {

        return alertRepository
                .findByDevice_PublicIdOrderByCreatedAtDesc(devicePublicId)
                .stream()
                .map(alert -> {

                    AlertResponseDto dto =
                            modelMapper.map(
                                    alert,
                                    AlertResponseDto.class);

                    dto.setDevicePublicId(
                            alert.getDevice().getPublicId());

                    return dto;
                })
                .toList();
    }

    @Override
    public void markAsRead(Long id) {

        Alert alert = alertRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Alert not found"));

        alert.setRead(true);

        alertRepository.save(alert);
    }

    @Override
    public void deleteAlert(Long id) {

        Alert alert = alertRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Alert not found"));

        alertRepository.delete(alert);
    }
}