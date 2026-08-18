package com.backend.geosentinel.geofence.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeoFenceResponseDto {

    private Long id;

    private String name;

    private Double latitude;

    private Double longitude;

    private Double radius;

    private Boolean active;

    private Boolean inside;

    private UUID devicePublicId;
}