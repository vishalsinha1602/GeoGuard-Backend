package com.backend.geosentinel.security.service;


import com.backend.geosentinel.exception.ResourceNotFoundException;
import com.backend.geosentinel.security.JWTService;
import com.backend.geosentinel.security.dto.LoginDto;
import com.backend.geosentinel.security.dto.LogoutResponseDto;
import com.backend.geosentinel.security.dto.SignUpRequestDto;
import com.backend.geosentinel.security.dto.SignUpResponseDto;
import com.backend.geosentinel.security.entity.RefreshToken;
import com.backend.geosentinel.security.entity.Role;
import com.backend.geosentinel.security.entity.User;
import com.backend.geosentinel.security.repository.RefreshTokenRepository;
import com.backend.geosentinel.security.repository.UserRepository;

import com.backend.geosentinel.util.AppUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;


@Service

@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    @Transactional
    public SignUpResponseDto signUp(SignUpRequestDto signUpRequestDto) {

        User user = userRepository.findByEmail(signUpRequestDto.getEmail()).orElse(null);

        if (user != null) {
            throw new RuntimeException("User is already present with same email id");
        }

        User newUser = modelMapper.map(signUpRequestDto, User.class);
        newUser.setRoles(Set.of(Role.GUEST));
        newUser.setPassword(passwordEncoder.encode(signUpRequestDto.getPassword()));
        newUser = userRepository.save(newUser);

        return modelMapper.map(newUser, SignUpResponseDto.class);
    }

    @Override
    @Transactional
    public String[] login(LoginDto loginDto) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getEmail(),
                        loginDto.getPassword()
                )
        );

        User user = (User) authentication.getPrincipal();

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);


        refreshTokenRepository.deleteByUser(user);

        RefreshToken token = RefreshToken.builder()
                .token(refreshToken)
                .user(user)
                .expiryDate(LocalDateTime.now().plusDays(30))
                .build();

        refreshTokenRepository.save(token);

        return new String[]{accessToken, refreshToken};
    }
    @Override
    @Transactional
    public String refreshToken(String refreshToken) {

        RefreshToken token = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Invalid refresh token"));

        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {

            refreshTokenRepository.delete(token);

            throw new RuntimeException("Refresh token expired");
        }

        return jwtService.generateAccessToken(token.getUser());
    }

    @Override
    @Transactional
    public LogoutResponseDto logout(HttpServletRequest request,
                                    HttpServletResponse response) {

        String refreshToken = AppUtil.getCookie(request, "refreshToken");

        if (refreshToken != null) {
            refreshTokenRepository.deleteByToken(refreshToken);
        }

        AppUtil.clearCookie(response, "accessToken");
        AppUtil.clearCookie(response, "refreshToken");

        return new LogoutResponseDto(true, "Logout successful");
    }
}

