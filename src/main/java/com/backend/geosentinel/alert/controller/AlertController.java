package com.backend.geosentinel.alert.controller;


import com.backend.geosentinel.alert.dto.AlertResponseDto;
import com.backend.geosentinel.alert.service.AlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertService alertService;

    @GetMapping("/device/{devicePublicId}")
    public ResponseEntity<List<AlertResponseDto>> getAlerts(
            @PathVariable UUID devicePublicId){

        return ResponseEntity.ok(
                alertService.getAlerts(devicePublicId)
        );
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long id){

        alertService.markAsRead(id);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlert(
            @PathVariable Long id){

        alertService.deleteAlert(id);

        return ResponseEntity.noContent().build();
    }

}