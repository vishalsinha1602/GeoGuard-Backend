package com.backend.geosentinel.geofence.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GeoFenceRequestDto {

    private String name;

    private Double latitude;

    private Double longitude;

    private Double radius;


    private UUID devicePublicId;
}