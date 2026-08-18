package com.backend.geosentinel.devices.entity;

import com.backend.geosentinel.alert.entity.Alert;
import com.backend.geosentinel.devices.entity.enums.DeviceStatus;
import com.backend.geosentinel.devices.entity.enums.DeviceType;
import com.backend.geosentinel.geofence.entity.Geofence;
import com.backend.geosentinel.locations.entity.Location;
import com.backend.geosentinel.security.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "devices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Public identifier exposed through APIs.
     * Never expose database id.
     */
    @Builder.Default
    @Column(
            nullable = false,
            unique = true,
            updatable = false
    )
    private UUID publicId = UUID.randomUUID();


    // =========================================================
    // DEVICE INFORMATION
    // =========================================================

    @NotBlank
    @Column(nullable = false)
    private String name;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeviceType type;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeviceStatus status;


    @Column(nullable = false)
    @Min(0)
    @Max(100)
    private Integer batteryLevel;


    private LocalDateTime lastSeen;


    // =========================================================
    // LOCATIONS
    // =========================================================

    @OneToMany(
            mappedBy = "device",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @Builder.Default
    private List<Location> locations =
            new ArrayList<>();


    // =========================================================
    // GEOFENCES
    // =========================================================

    @OneToMany(
            mappedBy = "device",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @Builder.Default
    private List<Geofence> geofences =
            new ArrayList<>();


    // =========================================================
    // ALERTS
    // =========================================================

    @OneToMany(
            mappedBy = "device",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @Builder.Default
    private List<Alert> alerts =
            new ArrayList<>();


    // =========================================================
    // TIMESTAMPS
    // =========================================================

    @CreationTimestamp
    @Column(
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;


    @UpdateTimestamp
    private LocalDateTime updatedAt;


    // =========================================================
    // OWNER
    // =========================================================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "owner_id",
            nullable = false
    )
    private User owner;


    // =========================================================
    // ACTIVE
    // =========================================================

    @Builder.Default
    @Column(nullable = false)
    private boolean active = true;


    // =========================================================
    // PRE PERSIST
    // =========================================================

    @PrePersist
    public void prePersist() {

        if (status == null) {

            status = DeviceStatus.OFFLINE;

        }

        if (batteryLevel == null) {

            batteryLevel = 100;

        }

        if (lastSeen == null) {

            lastSeen = LocalDateTime.now();

        }

        if (publicId == null) {

            publicId = UUID.randomUUID();

        }

    }
}