package com.kitsune.BanckLoad.application.service;

import com.kitsune.BanckLoad.domain.model.Loan.LoanStatus;
import com.kitsune.BanckLoad.infrastructure.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatisticsService {

    private final LoanRepository loanRepository;

    @Cacheable(value = "statistics", key = "'global'")
    @Transactional(readOnly = true)
    public Map<String, Object> getGlobalStatistics() {
        log.info("Calculando estadísticas globales desde la base de datos");

        Map<String, Object> stats = new HashMap<>();

        long totalLoans = loanRepository.count();
        long approvedLoans = loanRepository.countByStatus(LoanStatus.APPROVED);
        long pendingLoans = loanRepository.countByStatus(LoanStatus.PENDING);
        long rejectedLoans = loanRepository.countByStatus(LoanStatus.REJECTED);

        stats.put("totalLoans", totalLoans);
        stats.put("approvedLoans", approvedLoans);
        stats.put("pendingLoans", pendingLoans);
        stats.put("rejectedLoans", rejectedLoans);

        // Calcular porcentajes
        if (totalLoans > 0) {
            stats.put("approvalRate", (approvedLoans * 100.0) / totalLoans);
            stats.put("rejectionRate", (rejectedLoans * 100.0) / totalLoans);
        } else {
            stats.put("approvalRate", 0.0);
            stats.put("rejectionRate", 0.0);
        }

        return stats;
    }

    @Cacheable(value = "statistics", key = "'user:' + #username")
    @Transactional(readOnly = true)
    public Map<String, Object> getUserStatistics(String username) {
        log.info("Calculando estadísticas del usuario: {}", username);

        Map<String, Object> stats = new HashMap<>();

        long userTotalLoans = loanRepository.countByUser_Username(username);
        long userApprovedLoans = loanRepository.countByUser_UsernameAndStatus(username, LoanStatus.APPROVED);
        long userPendingLoans = loanRepository.countByUser_UsernameAndStatus(username, LoanStatus.PENDING);
        long userRejectedLoans = loanRepository.countByUser_UsernameAndStatus(username, LoanStatus.REJECTED);

        stats.put("totalLoans", userTotalLoans);
        stats.put("approvedLoans", userApprovedLoans);
        stats.put("pendingLoans", userPendingLoans);
        stats.put("rejectedLoans", userRejectedLoans);

        return stats;
    }
}

