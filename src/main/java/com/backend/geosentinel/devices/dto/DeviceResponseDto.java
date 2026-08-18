package com.backend.geosentinel.devices.dto;


import com.backend.geosentinel.devices.entity.enums.DeviceStatus;
import com.backend.geosentinel.devices.entity.enums.DeviceType;
import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

@Data
@JsonPropertyOrder({
        "publicId",
        "name",
        "type",
        "status",
        "batteryLevel",
        "lastSeen",
        "createdAt",
        "updatedAt"
})
public class DeviceResponseDto {

    private UUID publicId;
    private String name;
    private DeviceType type;
    private DeviceStatus status;
    private Integer batteryLevel;
    private LocalDateTime lastSeen;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


}
