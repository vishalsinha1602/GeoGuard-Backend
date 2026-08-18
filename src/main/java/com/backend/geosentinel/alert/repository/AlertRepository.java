package com.backend.geosentinel.alert.repository;


import com.backend.geosentinel.alert.entity.Alert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AlertRepository extends JpaRepository<Alert, Long> {

    List<Alert> findByDevice_PublicIdOrderByCreatedAtDesc(UUID devicePublicId);

}