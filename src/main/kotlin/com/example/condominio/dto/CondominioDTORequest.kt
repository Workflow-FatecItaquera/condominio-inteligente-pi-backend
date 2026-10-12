package com.example.condominio.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size

data class CondominioDTORequest(
    @NotBlank(message = "O campo nome precisa ser preenchido")
    @Size(min = 1, max = 100)
    val nome: String = "",

    @NotBlank(message = "O campo rua precisa ser preenchido")
    @Size(max = 80)
    val rua: String = "",

    @Positive(message = "O número precisa ser positivo")
    val numero: Int = 0,

    @NotBlank(message = "O campo bairro precisa ser preenchido")
    @Size(max = 80)
    val bairro: String = "",

    @NotBlank(message = "O campo cidade precisa ser preenchido")
    @Size(max = 80)
    val cidade: String = "",

    @NotBlank(message = "O campo cnpj precisa ser preenchido")
    @Size(max = 14)
    @Pattern(regexp = "\\d{14}", message = "O CNPJ deve ter 14 dígitos, apenas números")
    val cnpj: String = "",

    @Size(max = 255)
    val foto: String? = null,

    val active: Boolean = true
)