package com.example.condominio.dto

import java.time.LocalDateTime

data class OcorrenciaDTOResponse(
    val id: Long = 0,
    val condominioId: Long = 0,
    val descricao: String = "",
    val categoria: String = "",
    val status: String = "",
    val criadaEm: LocalDateTime = LocalDateTime.now(),
    val active: Boolean = true
)
