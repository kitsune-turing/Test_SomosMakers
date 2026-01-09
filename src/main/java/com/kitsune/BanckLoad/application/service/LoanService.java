package com.kitsune.BanckLoad.application.service;

import com.kitsune.BanckLoad.application.dto.LoanRequestDTO;
import com.kitsune.BanckLoad.application.dto.LoanResponseDTO;
import com.kitsune.BanckLoad.application.dto.LoanReviewDTO;
import com.kitsune.BanckLoad.domain.model.Loan;
import com.kitsune.BanckLoad.domain.model.Loan.LoanStatus;
import com.kitsune.BanckLoad.domain.model.User;
import com.kitsune.BanckLoad.infrastructure.repository.LoanRepository;
import com.kitsune.BanckLoad.infrastructure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanService {

    private final LoanRepository loanRepository;
    private final UserRepository userRepository;

    @Transactional
    @CacheEvict(value = {"loans", "statistics"}, allEntries = true)
    public LoanResponseDTO requestLoan(LoanRequestDTO request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Validar que el usuario no sea administrador
        if (user.getRoles().contains("ADMIN")) {
            log.warn("Intento de solicitud de préstamo por administrador: {}", username);
            throw new RuntimeException("Los administradores no pueden solicitar préstamos");
        }

        Loan loan = Loan.builder()
                .amount(request.getAmount())
                .term(request.getTerm())
                .purpose(request.getPurpose())
                .user(user)
                .status(LoanStatus.PENDING)
                .requestDate(LocalDateTime.now())
                .build();

        loan = loanRepository.save(loan);
        log.info("Préstamo solicitado: ID={}, Usuario={}, Monto={}",
                loan.getId(), username, request.getAmount());

        return LoanResponseDTO.fromEntity(loan);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "loans", key = "#username")
    public List<LoanResponseDTO> getUserLoans(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        log.info("Consultando préstamos del usuario: {}", username);
        return loanRepository.findByUser(user).stream()
                .map(LoanResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "loans", key = "'all'")
    public List<LoanResponseDTO> getAllLoans() {
        log.info("Consultando todos los préstamos");
        return loanRepository.findAll().stream()
                .map(LoanResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "loans", key = "'pending'")
    public List<LoanResponseDTO> getPendingLoans() {
        log.info("Consultando préstamos pendientes");
        return loanRepository.findByStatus(LoanStatus.PENDING).stream()
                .map(LoanResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "loans", key = "#id")
    public LoanResponseDTO getLoanById(Long id) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Préstamo no encontrado"));

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Verificar que el usuario tenga permiso para ver este préstamo
        if (!loan.getUser().getId().equals(user.getId()) &&
            !user.getRoles().contains("ADMIN")) {
            throw new RuntimeException("No tiene permiso para ver este préstamo");
        }

        return LoanResponseDTO.fromEntity(loan);
    }

    @Transactional
    @CacheEvict(value = {"loans", "statistics"}, allEntries = true)
    public LoanResponseDTO reviewLoan(Long loanId, LoanReviewDTO reviewDTO) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User admin = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Préstamo no encontrado"));

        if (loan.getStatus() != LoanStatus.PENDING) {
            throw new RuntimeException("Este préstamo ya ha sido revisado");
        }

        // Usar 'action' como campo principal
        String status = reviewDTO.getAction();
        LoanStatus newStatus;

        if ("APPROVED".equalsIgnoreCase(status)) {
            newStatus = LoanStatus.APPROVED;
            loan.setRejectionReason(null);
            log.info("Préstamo aprobado: ID={}, Admin={}", loanId, username);
        } else if ("REJECTED".equalsIgnoreCase(status)) {
            newStatus = LoanStatus.REJECTED;
            loan.setRejectionReason(reviewDTO.getRejectionReason());
            log.info("Préstamo rechazado: ID={}, Admin={}, Razón={}",
                    loanId, username, reviewDTO.getRejectionReason());
        } else {
            throw new RuntimeException("Acción inválida. Use APPROVED o REJECTED");
        }

        loan.setStatus(newStatus);
        loan.setReviewedAt(LocalDateTime.now());
        loan.setReviewedBy(admin);

        loan = loanRepository.save(loan);

        return LoanResponseDTO.fromEntity(loan);
    }
}
