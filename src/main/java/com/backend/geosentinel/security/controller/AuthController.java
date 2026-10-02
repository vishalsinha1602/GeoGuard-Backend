package com.backend.geosentinel.security.controller;



import com.backend.geosentinel.security.dto.*;
import com.backend.geosentinel.security.service.AuthServiceImpl;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthServiceImpl authService;

    @Value("${auth.refresh-cookie-secure:false}")
    private boolean refreshCookieSecure;

    @Value("${auth.refresh-cookie-same-site:Lax}")
    private String refreshCookieSameSite;

    @PostMapping("/signup")
    public ResponseEntity<SignUpResponseDto> signup(@RequestBody SignUpRequestDto signUpRequestDto) {
        return ResponseEntity.ok(authService.signUp(signUpRequestDto));
    }


    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(
            @RequestBody LoginDto loginDto,
            HttpServletResponse httpServletResponse) {

        String[] tokens = authService.login(loginDto);
        addRefreshCookie(tokens[1], httpServletResponse);

        return ResponseEntity.ok(
                new LoginResponseDto(tokens[0])
        );
    }

    private void addRefreshCookie(String refreshToken, HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie
                .from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(refreshCookieSecure)
                .sameSite(refreshCookieSameSite)
                .path("/")
                .maxAge(Duration.ofDays(30))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    @PostMapping("/logout")
    public ResponseEntity<LogoutResponseDto> logout(
            HttpServletRequest request,
            HttpServletResponse response) {

        LogoutResponseDto logoutResponse = authService.logout(request, response);

        return ResponseEntity.ok(logoutResponse);
    }





    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDto> refresh(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        String refreshToken = Optional.ofNullable(cookies)
                .stream()
                .flatMap(Arrays::stream)
                .filter(cookie -> "refreshToken".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElseThrow(() ->
                        new AuthenticationServiceException("Refresh token not found"));
        String accessToken = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(new LoginResponseDto(accessToken));
    }

}
