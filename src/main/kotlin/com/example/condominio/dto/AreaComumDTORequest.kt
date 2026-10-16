package com.example.condominio.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size

data class AreaComumDTORequest(
    @Positive(message = "O campo condominioId precisa ser informado")
    val condominioId: Long = 0,

    @NotBlank(message = "O campo nome precisa ser preenchido")
    @Size(min = 3, max = 100)
    val nome: String = "",

    @NotBlank(message = "O campo tipo precisa ser preenchido")
    @Size(max = 50)
    val tipo: String = "",

    @Positive
    val capacidade: Int = 0,

    val active: Boolean = true,
    @Size(max = 255)
    val foto: String? = null
)