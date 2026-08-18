package com.backend.geosentinel.geofence.service;

import com.backend.geosentinel.geofence.dto.GeoFenceRequestDto;
import com.backend.geosentinel.geofence.dto.GeoFenceResponseDto;

import java.util.List;
import java.util.UUID;

public interface GeofenceService {
    GeoFenceResponseDto createGeofence(GeoFenceRequestDto requestDto);


    List<GeoFenceResponseDto> getDeviceGeofences(
            UUID devicePublicId);

//    GeoFenceRequestDto getGeofenceById(Long id);



    void deleteGeofence(Long id);
}
