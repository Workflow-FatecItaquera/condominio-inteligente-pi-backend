package com.example.condominio.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.PastOrPresent
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import java.math.BigDecimal
import java.time.LocalDate

data class PagamentoRegistradoDTORequest(
    @Positive(message = "O campo cobrancaId precisa ser informado")
    val cobrancaId: Long = 0,

    @Positive
    val valor: BigDecimal = BigDecimal.ZERO,

    @PastOrPresent
    val dataPagamento: LocalDate = LocalDate.now(),

    @NotBlank(message = "O campo formaPagamento precisa ser preenchido")
    @Size(max = 30)
    val formaPagamento: String = "",

    @Positive(message = "O campo confirmadoPorId precisa ser informado")
    val confirmadoPorId: Long = 0
)