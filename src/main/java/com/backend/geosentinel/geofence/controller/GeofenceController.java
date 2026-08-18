package com.backend.geosentinel.geofence.controller;

import com.backend.geosentinel.geofence.dto.GeoFenceRequestDto;
import com.backend.geosentinel.geofence.dto.GeoFenceResponseDto;
import com.backend.geosentinel.geofence.service.GeofenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/geofences")
@RequiredArgsConstructor
public class GeofenceController {

    private final GeofenceService geofenceService;

    /**
     * Create Geofence
     */
    @PostMapping
    public ResponseEntity<GeoFenceResponseDto> createGeofence(
            @RequestBody GeoFenceRequestDto requestDto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(geofenceService.createGeofence(requestDto));
    }

    /**
     * Get All Geofences of a Device
     */
    @GetMapping("/device/{devicePublicId}")
    public ResponseEntity<List<GeoFenceResponseDto>> getDeviceGeofences(
            @PathVariable UUID devicePublicId) {

        return ResponseEntity.ok(
                geofenceService.getDeviceGeofences(devicePublicId)
        );
    }



    /**
     * Delete Geofence
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGeofence(
            @PathVariable Long id) {

        geofenceService.deleteGeofence(id);

        return ResponseEntity.noContent().build();
    }
}