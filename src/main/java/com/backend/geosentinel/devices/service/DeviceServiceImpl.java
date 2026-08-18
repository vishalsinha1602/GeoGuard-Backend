package com.backend.geosentinel.devices.service;

import com.backend.geosentinel.devices.dto.DeviceRequestDto;
import com.backend.geosentinel.devices.dto.DeviceResponseDto;
import com.backend.geosentinel.devices.entity.Device;
import com.backend.geosentinel.devices.entity.enums.DeviceStatus;
import com.backend.geosentinel.devices.repository.DeviceRepository;
import com.backend.geosentinel.exception.ResourceNotFoundException;
import com.backend.geosentinel.exception.UnAuthorisedException;
import com.backend.geosentinel.security.entity.User;
import com.backend.geosentinel.util.AppUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class DeviceServiceImpl implements DeviceService{

    private final ModelMapper modelMapper;
    private final DeviceRepository deviceRepository;


    @Override
    public DeviceResponseDto createDevice(DeviceRequestDto deviceRequestDto) {

        log.info("Creating device ");

        User currentUser = AppUtil.getCurrentUser();


        Device device = modelMapper.map(deviceRequestDto, Device.class);


        device.setOwner(currentUser);

        Device savedDevice = deviceRepository.save(device);

        return modelMapper.map(savedDevice, DeviceResponseDto.class);
    }


    @Override
    public List<DeviceResponseDto> getAllDevice() {
        User currentUser = AppUtil.getCurrentUser();

        List<Device> devices =
                deviceRepository.findByOwnerAndActiveTrue(currentUser);

        return devices.stream()

                .map(device -> {

                    DeviceResponseDto response =
                            modelMapper.map(device, DeviceResponseDto.class);

                    response.setStatus(
                            calculateStatus(device)
                    );

                    return response;

                })

                .toList();
    }




    @Override public DeviceResponseDto getDeviceById(UUID publicId)
    {
        User currentUser = AppUtil.getCurrentUser();

        Device device = deviceRepository
                .findByPublicIdAndOwnerAndActiveTrue(publicId, currentUser)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Device not found with id: " + publicId
                        ));

        DeviceResponseDto response =
                modelMapper.map(device, DeviceResponseDto.class);

        response.setStatus(
                calculateStatus(device)
        );

        return response;

    }


    @Override
    @Transactional
    public DeviceResponseDto updateDevice(UUID publicId,
                                          DeviceRequestDto request) {



        log.info("Updating device {}", publicId);

        User currentUser = AppUtil.getCurrentUser();

        Device device = deviceRepository
                .findByPublicIdAndOwnerAndActiveTrue(publicId, currentUser)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Device not found with public id: " + publicId
                        ));

        device.setName(request.getName());
        device.setType(request.getType());

        Device updatedDevice = deviceRepository.save(device);

        return modelMapper.map(updatedDevice, DeviceResponseDto.class);
    }





    @Override
    public void deleteDeviceById(UUID publicId) {

        log.info("Deleting device {}", publicId);

        User currentUser = AppUtil.getCurrentUser();

        Device device = deviceRepository
                .findByPublicIdAndOwnerAndActiveTrue(
                        publicId,
                        currentUser
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Device not found with id: " + publicId
                        )
                );

        deviceRepository.delete(device);

        log.info("Device {} permanently deleted", publicId);
    }


    private DeviceStatus calculateStatus(Device device) {

        if (device.getLastSeen() == null) {

            return DeviceStatus.OFFLINE;

        }

        return device.getLastSeen()
                .isAfter(LocalDateTime.now().minusSeconds(15))
                ? DeviceStatus.ONLINE
                : DeviceStatus.OFFLINE;

    }


}
