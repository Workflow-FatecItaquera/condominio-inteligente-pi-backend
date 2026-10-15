package com.example.condominio.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "encomendas")
data class Encomenda(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "apartamento_id", nullable = false)
    val apartamento: Apartamento = Apartamento(),

    @Column(length = 255, nullable = false)
    val descricao: String = "",

    @Column(length = 50, nullable = false)
    val codigo: String = "",

    @Column(nullable = false)
    val recebidaEm: LocalDateTime = LocalDateTime.now(),

    val retiradaEm: LocalDateTime? = null,

    @Column(length = 30, nullable = false)
    val status: String = "",

    @Column(nullable = false)
    val active: Boolean = true
)
