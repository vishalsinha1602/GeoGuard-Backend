package com.backend.geosentinel.locations.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class LocationRequestDto {
    @NotNull
    private UUID devicePublicId;

    @NotNull @Min(-90) @Max(90)
    private BigDecimal latitude;

    @NotNull @Min(-180) @Max(180)
    private BigDecimal longitude;

    @Min(0)
    private Double speed;

    @Min(0) @Max(100)
    private Integer batteryLevel;

    private Boolean sos;
}
