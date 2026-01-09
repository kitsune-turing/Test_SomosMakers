package com.kitsune.BanckLoad.application.controller;

import com.kitsune.BanckLoad.infrastructure.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class LoanControllerIntegrationTest {

    @Autowired
    private JwtService jwtService;

    @Test
    void contextLoads() {
        assertNotNull(jwtService);
    }

    @Test
    void testJwtServiceIsAvailable() {
        String token = jwtService.generateToken("testuser");
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }
}

