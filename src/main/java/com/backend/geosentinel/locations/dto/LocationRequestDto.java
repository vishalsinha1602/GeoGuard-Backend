package com.backend.geosentinel.locations.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class LocationRequestDto {
    private UUID devicePublicId;

    private BigDecimal latitude;

    private BigDecimal longitude;

    private Double speed;

    private Integer batteryLevel;
}
