package com.backend.geosentinel.security.service;

import com.backend.geosentinel.security.dto.LoginDto;
import com.backend.geosentinel.security.dto.LogoutResponseDto;
import com.backend.geosentinel.security.dto.SignUpRequestDto;
import com.backend.geosentinel.security.dto.SignUpResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
    SignUpResponseDto signUp(SignUpRequestDto signUpRequestDto);
    String[] login(LoginDto loginDto);
    String refreshToken(String refreshToken);
    LogoutResponseDto logout(HttpServletRequest request, HttpServletResponse response);
}
