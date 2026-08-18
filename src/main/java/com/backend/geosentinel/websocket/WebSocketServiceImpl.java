package com.backend.geosentinel.websocket;

import com.backend.geosentinel.alert.dto.AlertResponseDto;
import com.backend.geosentinel.devices.dto.DeviceLiveDto;
import com.backend.geosentinel.locations.dto.LocationResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;

import java.util.UUID;


@Service
@Slf4j
@RequiredArgsConstructor
@EnableWebSocketMessageBroker
public class WebSocketServiceImpl implements WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void sendLocationUpdate(
            UUID devicePublicId,
            DeviceLiveDto response
    ) {

        log.info("Sending location update for device {}", devicePublicId);

        messagingTemplate.convertAndSend(
                "/topic/location/" + devicePublicId,
                response
        );
    }


    @Override
    public void sendAlert(UUID devicePublicId, AlertResponseDto dto) {

        System.out.println("========== SEND ALERT ==========");

        // Device specific
        messagingTemplate.convertAndSend(
                "/topic/alerts/" + devicePublicId,
                dto
        );

//        // Dashboard
//        messagingTemplate.convertAndSend(
//                "/topic/alerts",
//                dto
//        );
    }
}