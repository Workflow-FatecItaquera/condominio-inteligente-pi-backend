package com.example.condominio.model

import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * Autorização concedida pelo responsável da unidade a um visitante específico (RN03),
 * com datas e horários permitidos (RN02) e possibilidade de cancelamento (RN05).
 */
@Entity
@Table(name = "autorizacoes_acesso")
data class AutorizacaoAcesso(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "visitante_id", nullable = false)
    val visitante: Visitante = Visitante(),

    @ManyToOne
    @JoinColumn(name = "unidade_id", nullable = false)
    val unidade: Apartamento = Apartamento(),

    @ManyToOne
    @JoinColumn(name = "responsavel_id")
    val responsavel: Usuario? = null,

    @Column(nullable = false)
    val dataInicio: LocalDate = LocalDate.now(),

    @Column(nullable = false)
    val dataFim: LocalDate = LocalDate.now(),

    @Column(nullable = false)
    val horaInicio: LocalTime = LocalTime.of(0, 0, 0),

    @Column(nullable = false)
    val horaFim: LocalTime = LocalTime.of(23, 59, 59),

    @Column(nullable = false)
    val permiteFimSemana: Boolean = false,

    @Column(nullable = false)
    val permiteFeriado: Boolean = false,

    val canceladaEm: LocalDateTime? = null
) {

    fun estaCancelada(): Boolean {
        return canceladaEm != null
    }

    /** RN05: cancelada vale em qualquer fase; depois, só a data decide o estado. */
    fun estadoEm(agora: LocalDateTime): EstadoAutorizacao {
        if (estaCancelada()) {
            return EstadoAutorizacao.CANCELADA
        }
        val data = agora.toLocalDate()
        if (data.isBefore(dataInicio)) {
            return EstadoAutorizacao.AGENDADA
        }
        if (data.isAfter(dataFim)) {
            return EstadoAutorizacao.EXPIRADA
        }
        return EstadoAutorizacao.VIGENTE
    }

    /** RN02: data inicial e data final são inclusivas. */
    fun cobreData(data: LocalDate): Boolean {
        return !data.isBefore(dataInicio) && !data.isAfter(dataFim)
    }

    /** RN02: hora inicial e hora final são inclusivas. */
    fun dentroDoHorario(hora: LocalTime): Boolean {
        return !hora.isBefore(horaInicio) && !hora.isAfter(horaFim)
    }

    fun cancelar(agora: LocalDateTime): AutorizacaoAcesso {
        return copy(canceladaEm = agora)
    }
}
