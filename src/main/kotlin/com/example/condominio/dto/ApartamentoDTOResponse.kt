package com.example.condominio.dto

data class ApartamentoDTOResponse(
    val id: Long = 0,
    val condominioId: Long = 0,
    val bloco: String = "",
    val numero: Int = 0,
    val andar: String = "",
    val active: Boolean = true
)