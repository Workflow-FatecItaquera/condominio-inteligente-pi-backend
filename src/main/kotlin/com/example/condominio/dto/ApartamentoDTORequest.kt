package com.example.condominio.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size

data class ApartamentoDTORequest(
    @Positive(message = "O campo condominioId precisa ser informado")
    val condominioId: Long = 0,

    @NotBlank(message = "O campo bloco precisa ser preenchido")
    @Size(max = 10)
    val bloco: String = "",

    @Positive(message = "O número precisa ser positivo")
    val numero: Int = 0,

    @NotBlank(message = "O campo andar precisa ser preenchido")
    @Size(max = 10)
    val andar: String = "",

    val active: Boolean = true
)