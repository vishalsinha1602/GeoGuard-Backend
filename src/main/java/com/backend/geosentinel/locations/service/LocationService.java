package com.backend.geosentinel.locations.service;

import com.backend.geosentinel.locations.dto.LocationRequestDto;
import com.backend.geosentinel.locations.dto.LocationResponseDto;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

public interface LocationService {
    LocationResponseDto saveLocation(@Valid LocationRequestDto request);

    LocationResponseDto getLatestLocation(UUID devicePublicId);

    List<LocationResponseDto> getLocationHistory(UUID devicePublicId);

    LocationResponseDto saveBrowserLocation(LocationRequestDto request);
}
