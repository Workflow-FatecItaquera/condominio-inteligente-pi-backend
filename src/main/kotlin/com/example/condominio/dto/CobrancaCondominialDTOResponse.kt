package com.example.condominio.dto

import java.math.BigDecimal
import java.time.LocalDate

data class CobrancaCondominialDTOResponse(
    val id: Long = 0,
    val apartamentoId: Long = 0,
    val competencia: String = "",
    val valor: BigDecimal = BigDecimal.ZERO,
    val vencimento: LocalDate = LocalDate.now(),
    val active: Boolean = true
)