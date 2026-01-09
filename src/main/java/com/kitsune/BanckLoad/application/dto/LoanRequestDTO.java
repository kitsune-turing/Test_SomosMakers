package com.kitsune.BanckLoad.application.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanRequestDTO {

    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "100.0", message = "El monto mínimo es 100")
    private BigDecimal amount;

    @NotNull(message = "El plazo es obligatorio")
    @Min(value = 1, message = "El plazo mínimo es 1 mes")
    private Integer term;

    @NotNull(message = "El propósito es obligatorio")
    private String purpose;
}
