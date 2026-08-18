package com.backend.geosentinel.locations.repository;




import com.backend.geosentinel.devices.entity.Device;
import com.backend.geosentinel.locations.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface LocationRepository extends JpaRepository<Location,Long> {
    Optional<Location> findTopByDeviceOrderByReceivedAtDesc(Device device);

    List<Location> findByDeviceOrderByReceivedAtDesc(Device device);
}
