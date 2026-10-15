package com.example.condominio.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size

data class OcorrenciaDTORequest(
    @Positive(message = "O campo condominioId precisa ser informado")
    val condominioId: Long = 0,

    @NotBlank(message = "O campo descricao precisa ser preenchido")
    @Size(min = 10, max = 255)
    val descricao: String = "",

    @NotBlank(message = "O campo categoria precisa ser preenchido")
    @Size(max = 50)
    val categoria: String = "",

    @NotBlank(message = "O campo status precisa ser preenchido")
    @Size(min = 3, max = 30)
    val status: String = ""
)
