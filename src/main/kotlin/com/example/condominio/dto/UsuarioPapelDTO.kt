package com.example.condominio.dto

import jakarta.validation.constraints.Pattern

data class UsuarioPapelDTO(
    @Pattern(
        regexp = "MORADOR|PORTARIA|SINDICO|ADMIN",
        message = "O papel deve ser MORADOR, PORTARIA, SINDICO ou ADMIN"
    )
    val papel: String = ""
)