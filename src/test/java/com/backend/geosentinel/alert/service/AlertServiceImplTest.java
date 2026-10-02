package com.backend.geosentinel.alert.service;

import com.backend.geosentinel.alert.repository.AlertRepository;
import com.backend.geosentinel.websocket.WebSocketService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AlertServiceImplTest {

    @Mock
    private AlertRepository alertRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private WebSocketService webSocketService;

    @InjectMocks
    private AlertServiceImpl alertService;

    @Test
    void clearAlertsForDevice_shouldDeleteAllAlertsForThatDevice() {
        UUID devicePublicId = UUID.fromString("11111111-1111-1111-1111-111111111111");

        alertService.clearAlertsForDevice(devicePublicId);

        verify(alertRepository).deleteByDevice_PublicId(devicePublicId);
    }
}
