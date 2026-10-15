package com.example.condominio.dto

import java.math.BigDecimal
import java.time.LocalDate

data class PagamentoRegistradoDTOResponse(
    val id: Long = 0,
    val cobrancaId: Long = 0,
    val valor: BigDecimal = BigDecimal.ZERO,
    val dataPagamento: LocalDate = LocalDate.now(),
    val formaPagamento: String = "",
    val confirmadoPorId: Long = 0,
    val active: Boolean = true
)