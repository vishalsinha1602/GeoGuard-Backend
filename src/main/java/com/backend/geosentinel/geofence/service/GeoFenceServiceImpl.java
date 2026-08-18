package com.backend.geosentinel.geofence.service;

import com.backend.geosentinel.devices.entity.Device;
import com.backend.geosentinel.devices.repository.DeviceRepository;
import com.backend.geosentinel.exception.ResourceNotFoundException;
import com.backend.geosentinel.geofence.dto.GeoFenceRequestDto;
import com.backend.geosentinel.geofence.dto.GeoFenceResponseDto;
import com.backend.geosentinel.geofence.entity.Geofence;
import com.backend.geosentinel.geofence.repository.GeofenceRepository;
import com.backend.geosentinel.locations.repository.LocationRepository;
import com.backend.geosentinel.util.GeofenceUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GeoFenceServiceImpl implements GeofenceService {

    private final GeofenceRepository geofenceRepository;
    private final DeviceRepository deviceRepository;
    private  final LocationRepository locationRepository;
    private final ModelMapper modelMapper;

    @Override
    public GeoFenceResponseDto createGeofence(
            GeoFenceRequestDto requestDto) {

        Device device = deviceRepository
                .findByPublicId(requestDto.getDevicePublicId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Device not found"));

        Geofence geofence = Geofence.builder()
                .name(requestDto.getName())
                .latitude(requestDto.getLatitude())
                .longitude(requestDto.getLongitude())
                .radius(requestDto.getRadius())
                .device(device)
                .build();

        // Default
        geofence.setInside(false);

        // Latest location of device
        locationRepository
                .findTopByDeviceOrderByReceivedAtDesc(device)
                .ifPresent(location -> {

                    boolean inside = GeofenceUtil.isInside(

                            location.getLatitude().doubleValue(),
                            location.getLongitude().doubleValue(),

                            geofence.getLatitude(),
                            geofence.getLongitude(),

                            geofence.getRadius()

                    );

                    geofence.setInside(inside);

                });

        Geofence saved = geofenceRepository.save(geofence);

        GeoFenceResponseDto response =
                modelMapper.map(saved, GeoFenceResponseDto.class);

        response.setDevicePublicId(saved.getDevice().getPublicId());
        response.setInside(saved.getInside());

        return response;
    }



    @Override
    public List<GeoFenceResponseDto> getDeviceGeofences(
            UUID devicePublicId) {

        return geofenceRepository
                .findByDevice_PublicId(devicePublicId)
                .stream()
                .map(geofence -> {

                    GeoFenceResponseDto dto =
                            modelMapper.map(
                                    geofence,
                                    GeoFenceResponseDto.class);

                    dto.setDevicePublicId(
                            geofence.getDevice().getPublicId());

                    dto.setInside(
                            geofence.getInside());

                    return dto;
                })
                .toList();
    }



//    @Override
//    public GeoFenceResponseDto getGeofenceById(
//            Long id) {
//
//        Geofence geofence =
//                geofenceRepository.findById(id)
//                        .orElseThrow(() ->
//                                new ResourceNotFoundException("Geofence not found"));
//
//        GeoFenceResponseDto dto =
//                modelMapper.map(
//                        geofence,
//                        GeoFenceResponseDto.class);
//
//        dto.setDevicePublicId(
//                geofence.getDevice().getPublicId());
//
//        return dto;
//    }



    @Override
    public void deleteGeofence(
            Long id) {

        Geofence geofence =
                geofenceRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Geofence not found"));

        geofenceRepository.delete(geofence);
    }
}