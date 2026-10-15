package com.example.condominio.dto

import java.time.LocalDateTime

data class VisitanteDTOResponse(
    val id: Long = 0,
    val nome: String = "",
    val visitaAutorizada: Boolean = false,
    val ultimaVisita: LocalDateTime? = null,
    val documento: String = "",
    val telefone: String? = null,
    val email: String? = null,
    val foto: String? = null,
    val dataRegistro: LocalDateTime? = null
)