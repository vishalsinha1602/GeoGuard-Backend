package com.backend.geosentinel.alert.entity;

import com.backend.geosentinel.alert.entity.enums.AlertType;
import com.backend.geosentinel.devices.entity.Device;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "alerts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // =========================================================
    // ALERT INFORMATION
    // =========================================================

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlertType type;


    @Column(nullable = false)
    private String title;


    @Column(nullable = false)
    private String message;


    @Builder.Default
    @Column(nullable = false)
    private Boolean read = false;


    // =========================================================
    // DEVICE
    // =========================================================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "device_id",
            nullable = false
    )
    private Device device;


    // =========================================================
    // CREATED AT
    // =========================================================

    @CreationTimestamp
    @Column(
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

}