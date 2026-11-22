package com.example.condominio.model

import jakarta.persistence.*
import java.time.LocalDate

/**
 * Lista de visitantes autorizados de uma unidade. O responsável deve atualizá-la
 * a cada 6 meses (RN01).
 */
@Entity
@Table(name = "listas_autorizados")
data class ListaAutorizados(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "unidade_id", nullable = false)
    val unidade: Apartamento = Apartamento(),

    @ManyToOne
    @JoinColumn(name = "responsavel_id")
    val responsavel: Usuario? = null,

    @Column(nullable = false)
    val atualizadaEm: LocalDate = LocalDate.now(),

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "lista_autorizados_visitantes",
        joinColumns = [JoinColumn(name = "lista_id")],
        inverseJoinColumns = [JoinColumn(name = "visitante_id")]
    )
    val visitantes: MutableSet<Visitante> = mutableSetOf(),

    @Column(nullable = false)
    val active: Boolean = true
) {

    /** RN01: 6 meses de calendário a partir da atualização (11/05 vale até 11/11). */
    fun validaAte(): LocalDate {
        return atualizadaEm.plusMonths(6)
    }

    /** RN01: o último dia de validade é inclusivo. */
    fun vigenteEm(data: LocalDate): Boolean {
        return !data.isAfter(validaAte())
    }

    fun contem(visitanteId: Long?): Boolean {
        if (visitanteId == null) {
            return false
        }
        return visitantes.any { it.id == visitanteId }
    }
}
