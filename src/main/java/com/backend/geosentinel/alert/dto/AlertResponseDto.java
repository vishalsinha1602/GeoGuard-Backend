package com.backend.geosentinel.alert.dto;

import com.backend.geosentinel.alert.entity.enums.AlertType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class AlertResponseDto {

    private Long id;

    private AlertType type;

    private String title;

    private String message;

    private Boolean read;

    private UUID devicePublicId;

    private LocalDateTime createdAt;

}