package com.backend.geosentinel.geofence.service;

import com.backend.geosentinel.alert.dto.AlertResponseDto;
import com.backend.geosentinel.alert.entity.Alert;
import com.backend.geosentinel.alert.entity.enums.AlertType;
import com.backend.geosentinel.alert.repository.AlertRepository;
import com.backend.geosentinel.devices.entity.Device;
import com.backend.geosentinel.geofence.entity.Geofence;
import com.backend.geosentinel.geofence.repository.GeofenceRepository;
import com.backend.geosentinel.locations.entity.Location;
import com.backend.geosentinel.locations.repository.LocationRepository;
import com.backend.geosentinel.websocket.WebSocketService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class GeofenceCheckerServiceImpl
        implements GeofenceCheckerService {

    private final GeofenceRepository geofenceRepository;
    private final LocationRepository locationRepository;
    private final AlertRepository alertRepository;
    private final WebSocketService webSocketService;
    private final ModelMapper modelMapper;

    @Override
    public void checkGeofences(Device device) {

        Location latestLocation = locationRepository
                .findTopByDeviceOrderByReceivedAtDesc(device)
                .orElse(null);

        if (latestLocation == null) {
            return;
        }

        List<Geofence> geofences =
                geofenceRepository.findByDevice_PublicId(
                        device.getPublicId()
                );

        for (Geofence geofence : geofences) {

            double distance = calculateDistance(

                    latestLocation.getLatitude().doubleValue(),
                    latestLocation.getLongitude().doubleValue(),

                    geofence.getLatitude(),
                    geofence.getLongitude()

            );

            boolean currentlyInside =
                    distance <= geofence.getRadius();

            /*
             OUTSIDE -> INSIDE
             */

            if (!geofence.getInside() && currentlyInside) {

                Alert alert = Alert.builder()

                        .device(device)

                        .type(AlertType.GEOFENCE_ENTER)

                        .title("Geofence Entered")

                        .message(device.getName()
                                + " entered "
                                + geofence.getName())

                        .build();

                // websocket me bhenge
                Alert savedAlert = alertRepository.save(alert);

                AlertResponseDto dto =
                        modelMapper.map(
                                savedAlert,
                                AlertResponseDto.class
                        );

                dto.setDevicePublicId(
                        device.getPublicId()
                );

                webSocketService.sendAlert(
                        device.getPublicId(),
                        dto
                );




                geofence.setInside(true);

                geofenceRepository.save(geofence);

            }

            /*
             INSIDE -> OUTSIDE
             */

            else if (geofence.getInside()
                    && !currentlyInside) {

                Alert alert = Alert.builder()

                        .device(device)

                        .type(AlertType.GEOFENCE_EXIT)

                        .title("Geofence Exited")

                        .message(device.getName()
                                + " exited "
                                + geofence.getName())

                        .build();


                // yha websocket ke through bhej rhe alert ko
                Alert savedAlert = alertRepository.save(alert);

                System.out.println("ALERT SAVED: " + savedAlert.getMessage());

                AlertResponseDto dto =
                        modelMapper.map(
                                savedAlert,
                                AlertResponseDto.class
                        );

                dto.setDevicePublicId(
                        device.getPublicId()
                );

                System.out.println("SENDING WS ALERT TO: /topic/alerts/" + device.getPublicId());
                webSocketService.sendAlert(
                        device.getPublicId(),
                        dto
                );

                geofence.setInside(false);

                geofenceRepository.save(geofence);

            }

        }

    }

    private double calculateDistance(

            double lat1,
            double lon1,
            double lat2,
            double lon2

    ) {

        final double EARTH_RADIUS = 6371000;

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a =
                Math.sin(dLat / 2) * Math.sin(dLat / 2)

                        +

                        Math.cos(Math.toRadians(lat1))
                                * Math.cos(Math.toRadians(lat2))

                                *

                                Math.sin(dLon / 2)
                                * Math.sin(dLon / 2);

        double c =
                2 * Math.atan2(
                        Math.sqrt(a),
                        Math.sqrt(1 - a)
                );

        return EARTH_RADIUS * c;

    }

}