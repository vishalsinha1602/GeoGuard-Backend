package com.backend.geosentinel.locations.controllers;

import com.backend.geosentinel.locations.dto.LocationRequestDto;
import com.backend.geosentinel.locations.dto.LocationResponseDto;
import com.backend.geosentinel.locations.service.LocationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;



@RestController
@RequestMapping("/devices/locations")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;


//     * Save current location

    @PostMapping
    public ResponseEntity<LocationResponseDto> saveLocation(
            @Valid @RequestBody LocationRequestDto request) {

        LocationResponseDto response =
                locationService.saveLocation(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


//     * Latest location of a device

    @GetMapping("/latest/{devicePublicId}")
    public ResponseEntity<LocationResponseDto> getLatestLocation(
            @PathVariable UUID devicePublicId) {

        return ResponseEntity.ok(
                locationService.getLatestLocation(devicePublicId)
        );
    }


//     * Complete location history

    @GetMapping("/history/{devicePublicId}")
    public ResponseEntity<List<LocationResponseDto>> getLocationHistory(
            @PathVariable UUID devicePublicId) {

        return ResponseEntity.ok(
                locationService.getLocationHistory(devicePublicId)
        );
    }


    @PostMapping("/browser")
    public ResponseEntity<LocationResponseDto> saveBrowserLocation(
            @RequestBody LocationRequestDto request){

        return ResponseEntity.ok(
                locationService.saveBrowserLocation(request)
        );
    }


//    DELETE /users/locations/history/{devicePublicId}
//    GET /users/locations/history/{devicePublicId}?from=...&to=...
}