package com.backend.geosentinel.geofence.repository;


import com.backend.geosentinel.geofence.entity.Geofence;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GeofenceRepository extends JpaRepository<Geofence, Long> {


    List<Geofence> findByDevice_PublicId(UUID publicId);
}
