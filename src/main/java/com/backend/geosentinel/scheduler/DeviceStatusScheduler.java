package com.backend.geosentinel.scheduler;

import com.backend.geosentinel.devices.dto.DeviceLiveDto;
import com.backend.geosentinel.devices.entity.Device;
import com.backend.geosentinel.devices.entity.enums.DeviceStatus;
import com.backend.geosentinel.devices.repository.DeviceRepository;
import com.backend.geosentinel.websocket.WebSocketService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DeviceStatusScheduler {

    private final DeviceRepository deviceRepository;
    private final WebSocketService webSocketService;

    @Scheduled(fixedRate = 5000)
    public void checkDevices() {

        List<Device> devices =
                deviceRepository.findByActiveTrue();

        for (Device device : devices) {

            DeviceStatus currentStatus =
                    device.getLastSeen() != null &&
                            device.getLastSeen().isAfter(
                                    LocalDateTime.now().minusSeconds(5))
                            ? DeviceStatus.ONLINE
                            : DeviceStatus.OFFLINE;

            // Send only if status changed
            if (device.getStatus() != currentStatus) {

                device.setStatus(currentStatus);

                deviceRepository.save(device);

                DeviceLiveDto dto =
                        DeviceLiveDto.builder()
                                .devicePublicId(device.getPublicId())
                                .batteryLevel(device.getBatteryLevel())
                                .status(currentStatus)
                                .lastSeen(device.getLastSeen())
                                .build();

                webSocketService.sendLocationUpdate(
                        device.getPublicId(),
                        dto
                );
            }
        }
    }
}