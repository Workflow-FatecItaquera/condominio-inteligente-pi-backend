package com.example.condominio.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.PastOrPresent
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/** Passos 1 a 4: portaria informa visitante (id ou só o documento), unidade e documento apresentado. */
data class AvaliarEntradaDTORequest(
    val visitanteId: Long? = null,

    @field:Positive(message = "O campo unidadeId precisa ser informado")
    val unidadeId: Long = 0,

    @field:NotBlank(message = "O campo documento precisa ser preenchido")
    val documento: String = ""
)

/** Passos 5 e 6: confirmação da liberação (revalida as regras antes de gravar). */
data class RegistrarEntradaDTORequest(
    @field:Positive(message = "O campo autorizacaoId precisa ser informado")
    val autorizacaoId: Long = 0,

    @field:NotBlank(message = "O campo documento precisa ser preenchido")
    val documento: String = ""
)

data class DecisaoAcessoDTOResponse(
    val permitido: Boolean = false,
    val motivo: String? = null,
    val mensagem: String = "",
    val autorizacaoId: Long? = null
)

data class RegistroAcessoDTOResponse(
    val id: Long = 0,
    val autorizacaoId: Long = 0,
    val visitanteId: Long = 0,
    val visitanteNome: String = "",
    val unidadeId: Long = 0,
    val porteiroId: Long = 0,
    val entradaEm: LocalDateTime? = null,
    val saidaEm: LocalDateTime? = null,
    val porteiroSaidaId: Long? = null
)

data class AutorizacaoDTORequest(
    @field:Positive(message = "O campo visitanteId precisa ser informado")
    val visitanteId: Long = 0,

    @field:Positive(message = "O campo unidadeId precisa ser informado")
    val unidadeId: Long = 0,

    @field:NotNull(message = "O campo dataInicio precisa ser informado")
    val dataInicio: LocalDate? = null,

    @field:NotNull(message = "O campo dataFim precisa ser informado")
    val dataFim: LocalDate? = null,

    @field:NotNull(message = "O campo horaInicio precisa ser informado")
    val horaInicio: LocalTime? = null,

    @field:NotNull(message = "O campo horaFim precisa ser informado")
    val horaFim: LocalTime? = null,

    val permiteFimSemana: Boolean = false,
    val permiteFeriado: Boolean = false
)

data class AutorizacaoDTOResponse(
    val id: Long = 0,
    val visitanteId: Long = 0,
    val unidadeId: Long = 0,
    val responsavelId: Long? = null,
    val dataInicio: LocalDate? = null,
    val dataFim: LocalDate? = null,
    val horaInicio: LocalTime? = null,
    val horaFim: LocalTime? = null,
    val permiteFimSemana: Boolean = false,
    val permiteFeriado: Boolean = false,
    val canceladaEm: LocalDateTime? = null,
    val estado: String = ""
)

data class ListaAutorizadosDTORequest(
    val visitanteIds: List<Long> = emptyList(),

    @field:PastOrPresent(message = "A data de atualização não pode ser futura")
    val atualizadaEm: LocalDate? = null
)

data class ListaAutorizadosDTOResponse(
    val id: Long = 0,
    val unidadeId: Long = 0,
    val atualizadaEm: LocalDate? = null,
    val validaAte: LocalDate? = null,
    val visitanteIds: List<Long> = emptyList()
)

data class FeriadoDTORequest(
    @field:NotNull(message = "O campo data precisa ser informado")
    val data: LocalDate? = null,

    @field:NotBlank(message = "O campo nome precisa ser preenchido")
    val nome: String = ""
)

data class FeriadoDTOResponse(
    val id: Long = 0,
    val data: LocalDate? = null,
    val nome: String = ""
)
