package com.kitsune.BanckLoad.application.controller;

import com.kitsune.BanckLoad.application.dto.LoanRequestDTO;
import com.kitsune.BanckLoad.application.dto.LoanResponseDTO;
import com.kitsune.BanckLoad.application.dto.LoanReviewDTO;
import com.kitsune.BanckLoad.application.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class LoanController {

    private final LoanService loanService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<LoanResponseDTO>> getUserLoans(Authentication authentication) {
        String username = authentication.getName();

        // Verificar si el usuario es admin
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        List<LoanResponseDTO> loans;
        if (isAdmin) {
            // Admin ve todas las solicitudes
            loans = loanService.getAllLoans();
        } else {
            // Usuario normal solo ve sus préstamos
            loans = loanService.getUserLoans(username);
        }

        return ResponseEntity.ok(loans);
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<LoanResponseDTO> createLoan(
            @Valid @RequestBody LoanRequestDTO request,
            Authentication authentication) {
        String username = authentication.getName();
        LoanResponseDTO response = loanService.requestLoan(request, username);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/request")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<LoanResponseDTO> requestLoan(
            @Valid @RequestBody LoanRequestDTO request,
            Authentication authentication) {
        String username = authentication.getName();
        LoanResponseDTO response = loanService.requestLoan(request, username);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-loans")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<LoanResponseDTO>> getMyLoans(Authentication authentication) {
        String username = authentication.getName();
        List<LoanResponseDTO> loans = loanService.getUserLoans(username);
        return ResponseEntity.ok(loans);
    }

    @GetMapping("/admin/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<LoanResponseDTO>> getPendingLoans() {
        List<LoanResponseDTO> loans = loanService.getPendingLoans();
        return ResponseEntity.ok(loans);
    }

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<LoanResponseDTO>> getAllLoans() {
        List<LoanResponseDTO> loans = loanService.getAllLoans();
        return ResponseEntity.ok(loans);
    }

    @PutMapping("/admin/review/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LoanResponseDTO> reviewLoan(
            @PathVariable Long id,
            @Valid @RequestBody LoanReviewDTO review) {
        LoanResponseDTO response = loanService.reviewLoan(id, review);
        return ResponseEntity.ok(response);
    }
}
