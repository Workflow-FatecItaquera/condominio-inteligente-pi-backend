package com.example.condominio.dto

import jakarta.validation.constraints.Future
import jakarta.validation.constraints.FutureOrPresent
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

data class ReservaDTORequest(
    @Positive(message = "O campo usuarioId precisa ser informado")
    val usuarioId: Long = 0,

    @Positive(message = "O campo areaComumId precisa ser informado")
    val areaComumId: Long = 0,

    @FutureOrPresent
    val inicio: LocalDateTime = LocalDateTime.now(),

    @Future
    val fim: LocalDateTime = LocalDateTime.now(),
    @NotBlank(message = "O campo motivo precisa ser informado")
    
    @Size(max = 30, message = "O campo motivo deve ter no máximo 30 caracteres")
    val motivo: String = "",

    @Size(max = 30, message = "O campo recorrência precisa ser informado")
    val recorrencia: String = "",

    @Size(max = 255)
    val active: Boolean = true,
)