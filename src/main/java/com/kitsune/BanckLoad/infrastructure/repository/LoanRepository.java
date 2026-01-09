package com.kitsune.BanckLoad.infrastructure.repository;

import com.kitsune.BanckLoad.domain.model.Loan;
import com.kitsune.BanckLoad.domain.model.Loan.LoanStatus;
import com.kitsune.BanckLoad.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByUser(User user);
    List<Loan> findByUserId(Long userId);
    List<Loan> findByStatus(LoanStatus status);
    List<Loan> findByUserIdAndStatus(Long userId, LoanStatus status);

    // Métodos de conteo para estadísticas con caché
    long countByStatus(LoanStatus status);
    long countByUser_Username(String username);
    long countByUser_UsernameAndStatus(String username, LoanStatus status);
}
