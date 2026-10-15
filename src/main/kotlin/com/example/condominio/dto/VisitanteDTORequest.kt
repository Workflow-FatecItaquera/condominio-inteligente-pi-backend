package com.example.condominio.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.PastOrPresent
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

data class VisitanteDTORequest(
    @NotBlank(message = "O campo nome precisa ser preenchido")
    @Size(min = 3, max = 100)
    val nome: String = "",

    val visitaAutorizada: Boolean = false,

    @PastOrPresent
    val ultimaVisita: LocalDateTime? = null,

    @NotBlank(message = "O campo documento precisa ser preenchido")
    @Size(min = 5, max = 20)
    val documento: String = "",

    @Size(min = 8, max = 20)
    val telefone: String? = null,

    @Size(max = 100)
    @Email
    val email: String? = null,

    @Size(max = 255)
    val foto: String? = null,

    val dataRegistro: LocalDateTime = LocalDateTime.now(),
    
)