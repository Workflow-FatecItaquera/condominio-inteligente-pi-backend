package com.example.condominio.dto

import java.time.LocalDateTime

data class ReservaDTOResponse(
    val id: Long = 0,
    val usuarioId: Long = 0,
    val areaComumId: Long = 0,
    val inicio: LocalDateTime = LocalDateTime.now(),
    val fim: LocalDateTime = LocalDateTime.now(),
    val motivo: String = "",
    val active: Boolean = true
)