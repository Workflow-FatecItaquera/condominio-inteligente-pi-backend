package com.example.condominio.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class UsuarioCadastroDTO(
    @NotBlank(message = "O campo nome precisa ser preenchido")
    @Size(min = 3, max = 100)
    val nome: String = "",

    @Pattern(regexp = "\\d{11}", message = "O CPF deve ter 11 dígitos, apenas números")
    val cpf: String = "",

    @NotBlank(message = "O campo email precisa ser preenchido")
    @Email
    val email: String = "",

    @Size(min = 6, max = 50, message = "A senha precisa ter entre 6 e 50 caracteres")
    val senha: String = "",

    @Size(min = 8, max = 20)
    val telefone: String? = null
)