package com.kitsune.BanckLoad.application.service;

import com.kitsune.BanckLoad.application.dto.LoanResponseDTO;
import com.kitsune.BanckLoad.domain.model.Loan;
import com.kitsune.BanckLoad.infrastructure.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReactiveLoanService {

    private final LoanRepository loanRepository;

    /**
     * Obtiene todos los préstamos de forma reactiva
     */
    public Flux<LoanResponseDTO> getAllLoansReactive() {
        return Flux.fromIterable(loanRepository.findAll())
                .map(LoanResponseDTO::fromEntity)
                .delayElements(Duration.ofMillis(100))
                .doOnNext(loan -> log.debug("Procesando préstamo reactivo: {}", loan.getId()));
    }

    /**
     * Obtiene un préstamo por ID de forma reactiva
     */
    public Mono<LoanResponseDTO> getLoanByIdReactive(Long id) {
        return Mono.fromCallable(() -> loanRepository.findById(id))
                .flatMap(optionalLoan -> optionalLoan
                        .map(loan -> Mono.just(LoanResponseDTO.fromEntity(loan)))
                        .orElse(Mono.error(new RuntimeException("Préstamo no encontrado"))))
                .doOnSuccess(loan -> log.debug("Préstamo encontrado reactivamente: {}", id));
    }

    /**
     * Búsqueda reactiva de préstamos con procesamiento en paralelo
     */
    public Flux<LoanResponseDTO> searchLoansReactive(String status) {
        return Flux.fromIterable(loanRepository.findAll())
                .filter(loan -> status == null || loan.getStatus().name().equalsIgnoreCase(status))
                .map(LoanResponseDTO::fromEntity)
                .delayElements(Duration.ofMillis(50));
    }
}

