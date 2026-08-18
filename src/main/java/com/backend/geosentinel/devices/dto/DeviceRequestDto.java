package com.backend.geosentinel.devices.dto;


import com.backend.geosentinel.devices.entity.enums.DeviceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DeviceRequestDto {

    @NotBlank
    private String name;
    @NotNull
    private DeviceType type;

}
