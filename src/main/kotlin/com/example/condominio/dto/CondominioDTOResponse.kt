package com.example.condominio.dto

data class CondominioDTOResponse(
    val id: Long = 0,
    val nome: String = "",
    val rua: String = "",
    val numero: Int = 0,
    val bairro: String = "",
    val cidade: String = "",
    val cnpj: String = "",
    val foto: String? = null,
    val active: Boolean = true
)