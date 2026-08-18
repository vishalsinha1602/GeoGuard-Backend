package com.backend.geosentinel.security.repository;

import com.backend.geosentinel.security.entity.RefreshToken;
import com.backend.geosentinel.security.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;


public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {


    void deleteByUser(User user);

    Optional<RefreshToken> findByToken(String token);

    void deleteByToken(String refreshToken);
}
