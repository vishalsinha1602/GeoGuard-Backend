package com.backend.geosentinel.devices.dto;

import com.backend.geosentinel.devices.entity.enums.DeviceStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class DeviceLiveDto {

    private UUID devicePublicId;

    private BigDecimal latitude;

    private BigDecimal longitude;

    private Double speed;

    private Integer batteryLevel;

    private DeviceStatus status;

    private LocalDateTime lastSeen;

}