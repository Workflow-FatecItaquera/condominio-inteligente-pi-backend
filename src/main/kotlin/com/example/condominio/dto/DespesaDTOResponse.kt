package com.example.condominio.dto

import java.math.BigDecimal
import java.time.LocalDate

data class DespesaDTOResponse(
    val id: Long = 0,
    val condominioId: Long = 0,
    val descricao: String = "",
    val categoria: String = "",
    val valor: BigDecimal = BigDecimal.ZERO,
    val vencimento: LocalDate = LocalDate.now(),
    val dataPagamento: LocalDate? = null,
    val active: Boolean = true
)