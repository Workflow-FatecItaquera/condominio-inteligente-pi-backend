package com.example.condominio.model

import jakarta.persistence.*
import java.time.LocalDateTime

/**
 * Registro de uma visita (RN04): a entrada cria o registro e a saída é gravada
 * no mesmo registro, encerrando a visita.
 */
@Entity
@Table(name = "registros_acesso")
data class RegistroAcesso(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "autorizacao_id", nullable = false)
    val autorizacao: AutorizacaoAcesso = AutorizacaoAcesso(),

    @Column(nullable = false)
    val unidadeId: Long = 0,

    @Column(nullable = false)
    val porteiroId: Long = 0,

    @Column(nullable = false)
    val entradaEm: LocalDateTime = LocalDateTime.now(),

    val saidaEm: LocalDateTime? = null,

    val porteiroSaidaId: Long? = null
) {

    fun visitaAberta(): Boolean {
        return saidaEm == null
    }

    fun registrarSaida(agora: LocalDateTime, porteiroId: Long): RegistroAcesso {
        return copy(saidaEm = agora, porteiroSaidaId = porteiroId)
    }
}
