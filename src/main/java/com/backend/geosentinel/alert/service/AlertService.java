package com.backend.geosentinel.alert.service;

import com.backend.geosentinel.alert.dto.AlertResponseDto;
import com.backend.geosentinel.alert.entity.enums.AlertType;
import com.backend.geosentinel.devices.entity.Device;

import java.util.List;
import java.util.UUID;

public interface AlertService {

    void createAlert(
            Device device,
            AlertType type,
            String title,
            String message
    );

    List<AlertResponseDto> getAlerts(UUID devicePublicId);

    void markAsRead(Long id);

    void deleteAlert(Long id);
}
