package com.example.condominio.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.PastOrPresent
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import java.math.BigDecimal
import java.time.LocalDate

data class DespesaDTORequest(
    @Positive(message = "O campo condominioId precisa ser informado")
    val condominioId: Long = 0,

    @NotBlank(message = "O campo descricao precisa ser preenchido")
    @Size(max = 255)
    val descricao: String = "",

    @NotBlank(message = "O campo categoria precisa ser preenchido")
    @Size(max = 50)
    val categoria: String = "",

    @Positive
    val valor: BigDecimal = BigDecimal.ZERO,

    val vencimento: LocalDate = LocalDate.now(),

    @PastOrPresent
    val dataPagamento: LocalDate? = null
)