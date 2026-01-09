package com.kitsune.BanckLoad.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanReviewDTO {

    @NotBlank(message = "La acción es obligatoria (APPROVED o REJECTED)")
    private String action;

    private String rejectionReason;
}

