package com.backend.geosentinel.locations.service;

import com.backend.geosentinel.devices.dto.DeviceLiveDto;
import com.backend.geosentinel.devices.entity.Device;
import com.backend.geosentinel.devices.entity.enums.DeviceStatus;
import com.backend.geosentinel.devices.repository.DeviceRepository;
import com.backend.geosentinel.exception.ResourceNotFoundException;
import com.backend.geosentinel.geofence.service.GeofenceCheckerService;
import com.backend.geosentinel.geofence.service.GeofenceService;
import com.backend.geosentinel.locations.dto.LocationRequestDto;
import com.backend.geosentinel.locations.dto.LocationResponseDto;
import com.backend.geosentinel.locations.entity.Location;
import com.backend.geosentinel.locations.repository.LocationRepository;
import com.backend.geosentinel.security.entity.User;
import com.backend.geosentinel.util.AppUtil;
import com.backend.geosentinel.websocket.WebSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;
    private final GeofenceCheckerService  geofenceCheckerService;
    private final DeviceRepository deviceRepository;
    private final WebSocketService webSocketService;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public LocationResponseDto saveLocation(LocationRequestDto request) {

        log.info("Saving current location {}", request.getDevicePublicId());

        User currentUser = AppUtil.getCurrentUser();

        Device device = deviceRepository
                .findByPublicIdAndOwnerAndActiveTrue(
                        request.getDevicePublicId(),
                        currentUser
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException("Device not found"));

        // Save Location
        Location location = Location.builder()
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .speed(request.getSpeed())
                .device(device)
                .build();

        Location savedLocation = locationRepository.save(location);
        System.out.println("========== CHECK GEOFENCE ==========");

        //for alert

        geofenceCheckerService.checkGeofences(device);

        // Update Device
        device.setLastSeen(savedLocation.getReceivedAt());
        device.setBatteryLevel(request.getBatteryLevel());

        deviceRepository.save(device);

        // REST Response
        LocationResponseDto response =
                modelMapper.map(savedLocation, LocationResponseDto.class);

        response.setBatteryLevel(device.getBatteryLevel());
        response.setStatus(DeviceStatus.ONLINE);
        response.setLastSeen(device.getLastSeen());

        // WebSocket Response
        DeviceLiveDto liveResponse = DeviceLiveDto.builder()
                .devicePublicId(device.getPublicId())
                .latitude(savedLocation.getLatitude())
                .longitude(savedLocation.getLongitude())
                .speed(savedLocation.getSpeed())
                .batteryLevel(device.getBatteryLevel())
                .status(DeviceStatus.ONLINE)
                .lastSeen(device.getLastSeen())
                .build();

        webSocketService.sendLocationUpdate(
                device.getPublicId(),
                liveResponse
        );

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public LocationResponseDto getLatestLocation(UUID devicePublicId) {

        log.info("Getting latest location for device {}", devicePublicId);

        User currentUser = AppUtil.getCurrentUser();

        Device device = deviceRepository
                .findByPublicIdAndOwnerAndActiveTrue(
                        devicePublicId,
                        currentUser
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException("Device not found"));

        Location location = locationRepository
                .findTopByDeviceOrderByReceivedAtDesc(device)
                .orElseThrow(() ->
                        new ResourceNotFoundException("No location found"));

        LocationResponseDto response =
                modelMapper.map(location, LocationResponseDto.class);

        response.setBatteryLevel(device.getBatteryLevel());
        response.setStatus(device.getStatus());
        response.setLastSeen(device.getLastSeen());

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<LocationResponseDto> getLocationHistory(UUID devicePublicId) {

        log.info("Getting location history for device {}", devicePublicId);

        User currentUser = AppUtil.getCurrentUser();

        Device device = deviceRepository
                .findByPublicIdAndOwnerAndActiveTrue(
                        devicePublicId,
                        currentUser
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException("Device not found"));

        List<Location> locations =
                locationRepository.findByDeviceOrderByReceivedAtDesc(device);

        return locations.stream()
                .map(location -> {

                    LocationResponseDto dto =
                            modelMapper.map(location, LocationResponseDto.class);

                    dto.setBatteryLevel(device.getBatteryLevel());
                    dto.setStatus(device.getStatus());
                    dto.setLastSeen(device.getLastSeen());

                    return dto;

                })
                .toList();
    }

    @Override
    @Transactional
    public LocationResponseDto saveBrowserLocation(LocationRequestDto request) {
        log.info("Browser Location {}", request.getDevicePublicId());

        Device device = deviceRepository
                .findByPublicId(request.getDevicePublicId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Device not found"));

        Location location = Location.builder()
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .speed(request.getSpeed())
                .device(device)
                .build();

        Location savedLocation = locationRepository.save(location);

        geofenceCheckerService.checkGeofences(device);

        device.setLastSeen(savedLocation.getReceivedAt());
        device.setBatteryLevel(request.getBatteryLevel());

        deviceRepository.save(device);

        LocationResponseDto response =
                modelMapper.map(savedLocation, LocationResponseDto.class);

        response.setBatteryLevel(device.getBatteryLevel());
        response.setStatus(DeviceStatus.ONLINE);
        response.setLastSeen(device.getLastSeen());

        DeviceLiveDto live = DeviceLiveDto.builder()
                .devicePublicId(device.getPublicId())
                .latitude(savedLocation.getLatitude())
                .longitude(savedLocation.getLongitude())
                .speed(savedLocation.getSpeed())
                .batteryLevel(device.getBatteryLevel())
                .status(DeviceStatus.ONLINE)
                .lastSeen(device.getLastSeen())
                .build();

        webSocketService.sendLocationUpdate(
                device.getPublicId(),
                live
        );

        return response;
    }


}