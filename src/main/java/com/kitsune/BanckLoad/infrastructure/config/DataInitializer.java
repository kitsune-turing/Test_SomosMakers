package com.kitsune.BanckLoad.infrastructure.config;

import com.kitsune.BanckLoad.domain.model.Loan;
import com.kitsune.BanckLoad.domain.model.User;
import com.kitsune.BanckLoad.infrastructure.repository.LoanRepository;
import com.kitsune.BanckLoad.infrastructure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final LoanRepository loanRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            log.info("Inicializando datos de prueba...");

            // Crear usuario administrador
            User admin = User.builder()
                    .username("admin")
                    .email("admin@test.com")
                    .password(passwordEncoder.encode("123"))
                    .fullName("Administrador del Sistema")
                    .roles(Set.of("ADMIN", "USER"))
                    .enabled(true)
                    .build();
            userRepository.save(admin);
            log.info("Usuario administrador creado: admin@test.com / 123");

            // Crear usuario regular
            User user = User.builder()
                    .username("usuario")
                    .email("usuario@test.com")
                    .password(passwordEncoder.encode("123"))
                    .fullName("Usuario de Prueba")
                    .roles(Set.of("USER"))
                    .enabled(true)
                    .build();
            userRepository.save(user);
            log.info("Usuario regular creado: usuario@test.com / 123");

            // Crear préstamos de prueba
            LocalDateTime now = LocalDateTime.now();

            Loan loan1 = Loan.builder()
                    .amount(new BigDecimal("5000.00"))
                    .term(12)
                    .purpose("Préstamo para mejoras en el hogar")
                    .user(user)
                    .status(Loan.LoanStatus.PENDING)
                    .requestDate(now.minusDays(5))
                    .updatedAt(now.minusDays(5))
                    .build();
            loanRepository.save(loan1);

            Loan loan2 = Loan.builder()
                    .amount(new BigDecimal("10000.00"))
                    .term(24)
                    .purpose("Préstamo para inversión en negocio")
                    .user(user)
                    .status(Loan.LoanStatus.APPROVED)
                    .reviewedBy(admin)
                    .requestDate(now.minusDays(10))
                    .reviewedAt(now.minusDays(2))
                    .updatedAt(now.minusDays(2))
                    .build();
            loanRepository.save(loan2);

            Loan loan3 = Loan.builder()
                    .amount(new BigDecimal("3000.00"))
                    .term(6)
                    .purpose("Préstamo para gastos médicos")
                    .user(user)
                    .status(Loan.LoanStatus.PENDING)
                    .requestDate(now.minusDays(3))
                    .updatedAt(now.minusDays(3))
                    .build();
            loanRepository.save(loan3);

            Loan loan4 = Loan.builder()
                    .amount(new BigDecimal("15000.00"))
                    .term(36)
                    .purpose("Préstamo para compra de vehículo")
                    .user(user)
                    .status(Loan.LoanStatus.REJECTED)
                    .rejectionReason("Monto excede el límite permitido")
                    .reviewedBy(admin)
                    .requestDate(now.minusDays(7))
                    .reviewedAt(now.minusDays(1))
                    .updatedAt(now.minusDays(1))
                    .build();
            loanRepository.save(loan4);

            log.info("Datos de prueba inicializados correctamente");
            log.info("Puede iniciar sesión con:");
            log.info("  - admin@test.com / 123 (Administrador)");
            log.info("  - usuario@test.com / 123 (Usuario)");
        }
    }
}
