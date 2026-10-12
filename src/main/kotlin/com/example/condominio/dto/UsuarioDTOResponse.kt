package com.example.condominio.dto

import java.time.LocalDateTime

data class UsuarioDTOResponse(
    val id: Long = 0,
    val nome: String = "",
    val cpf: String = "",
    val email: String = "",
    val telefone: String? = null,
    val papeis: String = "",
    val foto: String? = null,
    val active: Boolean = true,
    val dataRegistro: LocalDateTime? = null
)