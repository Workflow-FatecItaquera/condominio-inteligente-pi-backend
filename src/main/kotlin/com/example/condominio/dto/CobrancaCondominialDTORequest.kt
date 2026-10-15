package com.example.condominio.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import java.math.BigDecimal
import java.time.LocalDate

data class CobrancaCondominialDTORequest(
    @Positive(message = "O campo apartamentoId precisa ser informado")
    val apartamentoId: Long = 0,

    @NotBlank(message = "O campo competencia precisa ser preenchido")
    @Size(max = 7)
    @Pattern(regexp = "\\d{4}-\\d{2}", message = "A competência deve estar no formato AAAA-MM")
    val competencia: String = "",

    @Positive
    val valor: BigDecimal = BigDecimal.ZERO,

    val vencimento: LocalDate = LocalDate.now()
)