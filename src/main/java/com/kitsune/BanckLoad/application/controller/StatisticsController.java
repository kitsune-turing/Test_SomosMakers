package com.kitsune.BanckLoad.application.controller;

import com.kitsune.BanckLoad.application.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
@Slf4j
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/global")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getGlobalStatistics() {
        log.info("Solicitando estadísticas globales (con caché)");
        return ResponseEntity.ok(statisticsService.getGlobalStatistics());
    }

    @GetMapping("/user")
    public ResponseEntity<Map<String, Object>> getUserStatistics(@AuthenticationPrincipal UserDetails userDetails) {
        log.info("Solicitando estadísticas del usuario: {} (con caché)", userDetails.getUsername());
        return ResponseEntity.ok(statisticsService.getUserStatistics(userDetails.getUsername()));
    }
}

