package com.backend.geosentinel.devices.service;

import com.backend.geosentinel.devices.dto.DeviceRequestDto;
import com.backend.geosentinel.devices.dto.DeviceResponseDto;
import com.backend.geosentinel.devices.entity.Device;
import com.backend.geosentinel.security.entity.User;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeviceService {
    DeviceResponseDto createDevice(DeviceRequestDto deviceRequestDto);

    List<DeviceResponseDto> getAllDevice();

    DeviceResponseDto getDeviceById(UUID publicId);

    void deleteDeviceById(UUID publicId);

    DeviceResponseDto updateDevice(UUID publicId, @Valid DeviceRequestDto request);
}
