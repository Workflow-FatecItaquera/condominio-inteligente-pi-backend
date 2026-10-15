package com.example.condominio.dto

import java.time.LocalDateTime

data class EncomendaDTOResponse(
    val id: Long = 0,
    val apartamentoId: Long = 0,
    val descricao: String = "",
    val codigo: String = "",
    val recebidaEm: LocalDateTime = LocalDateTime.now(),
    val retiradaEm: LocalDateTime? = null,
    val status: String = "",
    val active: Boolean = true
)
