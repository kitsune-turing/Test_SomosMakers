package com.kitsune.BanckLoad.application.dto;

import com.kitsune.BanckLoad.domain.model.Loan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanResponseDTO implements Serializable {

    private Long id;
    private BigDecimal amount;
    private Integer term;
    private String purpose;
    private String status;
    private String statusDisplayName;
    private String rejectionReason;
    private LocalDateTime requestDate;
    private LocalDateTime reviewedAt;
    private String reviewedByUsername;
    private Long userId;
    private String username;

    public static LoanResponseDTO fromEntity(Loan loan) {
        return LoanResponseDTO.builder()
                .id(loan.getId())
                .amount(loan.getAmount())
                .term(loan.getTerm())
                .purpose(loan.getPurpose())
                .status(loan.getStatus().name())
                .statusDisplayName(loan.getStatus().getDisplayName())
                .rejectionReason(loan.getRejectionReason())
                .requestDate(loan.getRequestDate())
                .reviewedAt(loan.getReviewedAt())
                .reviewedByUsername(loan.getReviewedBy() != null ? loan.getReviewedBy().getUsername() : null)
                .userId(loan.getUser().getId())
                .username(loan.getUser().getUsername())
                .build();
    }
}
