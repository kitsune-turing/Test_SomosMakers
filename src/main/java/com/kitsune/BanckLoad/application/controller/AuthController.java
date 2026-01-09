package com.kitsune.BanckLoad.application.controller;

import com.kitsune.BanckLoad.application.dto.AuthResponseDTO;
import com.kitsune.BanckLoad.application.dto.LoginRequestDTO;
import com.kitsune.BanckLoad.application.dto.RegisterRequestDTO;
import com.kitsune.BanckLoad.application.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request) {
        try {
            AuthResponseDTO response = authService.register(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(
                AuthResponseDTO.builder()
                    .token(null)
                    .username(null)
                    .email(null)
                    .fullName(e.getMessage())
                    .build()
            );
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        try {
            AuthResponseDTO response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(
                AuthResponseDTO.builder()
                    .token(null)
                    .username(null)
                    .email(null)
                    .fullName(e.getMessage())
                    .build()
            );
        }
    }
}

