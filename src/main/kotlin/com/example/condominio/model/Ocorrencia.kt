package com.example.condominio.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "ocorrencias")
data class Ocorrencia(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "condominio_id", nullable = false)
    val condominio: Condominio = Condominio(),

    @Column(length = 255, nullable = false)
    val descricao: String = "",

    @Column(length = 50, nullable = false)
    val categoria: String = "",

    @Column(length = 30, nullable = false)
    val status: String = "",

    @Column(nullable = false)
    val criadaEm: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    val active: Boolean = true
)
