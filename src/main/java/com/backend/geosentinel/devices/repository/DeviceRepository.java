package com.backend.geosentinel.devices.repository;

import com.backend.geosentinel.devices.entity.Device;
import com.backend.geosentinel.security.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeviceRepository extends JpaRepository<Device,Integer> {


    List<Device> findByOwnerAndActiveTrue(User owner);

    Optional<Device> findByPublicIdAndOwnerAndActiveTrue(UUID publicId, User currentUser);

    List<Device> findByActiveTrue();

    Optional<Device> findByPublicId(UUID devicePublicId);


}
