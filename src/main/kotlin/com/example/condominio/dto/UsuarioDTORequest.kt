package com.example.condominio.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class UsuarioDTORequest(
    @NotBlank(message = "O campo nome precisa ser preenchido")
    @Size(min = 2, max = 100)
    val nome: String = "",

    @Pattern(regexp = "\\d{11}", message = "O CPF deve ter 11 dígitos, apenas números")
    val cpf: String = "",

    @Size(min = 8, max = 20)
    val telefone: String? = null,

    @Size(max = 255)
    val foto: String? = null
)