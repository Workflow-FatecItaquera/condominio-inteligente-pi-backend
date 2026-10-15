package com.example.condominio.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.PastOrPresent
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

data class EncomendaDTORequest(
    @Positive(message = "O campo apartamentoId precisa ser informado")
    val apartamentoId: Long = 0,

    @NotBlank(message = "O campo descricao precisa ser preenchido")
    @Size(max = 255)
    val descricao: String = "",

    @NotBlank(message = "O campo codigo precisa ser preenchido")
    @Size(max = 50)
    val codigo: String = "",

    @PastOrPresent
    val recebidaEm: LocalDateTime = LocalDateTime.now(),

    @PastOrPresent
    val retiradaEm: LocalDateTime? = null,

    @NotBlank(message = "O campo status precisa ser preenchido")
    @Size(min = 3, max = 30)
    val status: String = ""
)
