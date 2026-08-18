package com.backend.geosentinel.devices.controller;


import com.backend.geosentinel.devices.dto.DeviceRequestDto;
import com.backend.geosentinel.devices.dto.DeviceResponseDto;
import com.backend.geosentinel.devices.service.DeviceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;
//    POST   /api/v1/devices
//
//    GET    /api/v1/devices
//
//    GET    /api/v1/devices/{id}
//
//    PUT    /api/v1/devices/{id}
//
//    DELETE /api/v1/devices/{id}


    @PostMapping
    public ResponseEntity<DeviceResponseDto> createDevice(  @Valid    //use @Valid ehn required validation
             @RequestBody DeviceRequestDto deviceRequestDto) {

        DeviceResponseDto response = deviceService.createDevice(deviceRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    @GetMapping
    public ResponseEntity<List<DeviceResponseDto>> getMyDevices() {
        return ResponseEntity.ok(deviceService.getAllDevice());
    }

    @GetMapping("/{publicId}")
    public ResponseEntity<DeviceResponseDto> getDevice( @Valid
            @PathVariable UUID publicId) {
        return ResponseEntity.ok(deviceService.getDeviceById(publicId));
    }

    @PatchMapping("/{publicId}")
    public ResponseEntity<DeviceResponseDto> updateDevice(
            @PathVariable UUID publicId,
            @RequestBody DeviceRequestDto request) {

        return ResponseEntity.ok(deviceService.updateDevice(publicId, request));
    }

    @DeleteMapping("/{publicId}")
    public ResponseEntity<Void> deleteDevice(
            @PathVariable UUID publicId) {

        deviceService.deleteDeviceById(publicId);

        return ResponseEntity.noContent().build();
    }

}
