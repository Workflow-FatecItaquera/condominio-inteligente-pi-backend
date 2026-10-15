package com.example.condominio.dto

data class AreaComumDTOResponse(
    val id: Long = 0,
    val condominioId: Long = 0,
    val nome: String = "",
    val tipo: String = "",
    val capacidade: Int = 0,
    val active: Boolean = true,
    val foto: String? = null
)