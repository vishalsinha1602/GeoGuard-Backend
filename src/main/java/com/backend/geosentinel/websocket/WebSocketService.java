package com.backend.geosentinel.websocket;


import com.backend.geosentinel.alert.dto.AlertResponseDto;
import com.backend.geosentinel.devices.dto.DeviceLiveDto;
import com.backend.geosentinel.locations.dto.LocationResponseDto;

import java.util.UUID;

public interface WebSocketService {

    void sendLocationUpdate(
            UUID devicePublicId,
            DeviceLiveDto response
    );

    void sendAlert(
            UUID devicePublicId,
            AlertResponseDto dto);

}