package com.backend.geosentinel.locations.dto;

import com.backend.geosentinel.devices.entity.enums.DeviceStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationResponseDto {

    private BigDecimal latitude;

    private BigDecimal longitude;

    private Double speed;

    private LocalDateTime receivedAt;

    // NEW
    private Integer batteryLevel;

    private DeviceStatus status;

    private LocalDateTime lastSeen;
}