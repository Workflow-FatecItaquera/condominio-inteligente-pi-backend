package com.example.condominio.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "visitantes")
data class Visitante(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(length = 100, nullable = false)
    val nome: String = "",

    @Column(nullable = false)
    val visitaAutorizada: Boolean = false,

    val ultimaVisita: LocalDateTime? = null,

    @Column(length = 20, nullable = false)
    val documento: String = "",

    @Column(length = 20)
    val telefone: String? = null,

    @Column(length = 100)
    val email: String? = null,

    @Column(length = 255)
    val foto: String? = null,

    @Column(nullable = false)
    val active: Boolean = true,

    @Column(nullable = false)
    val dataRegistro: LocalDateTime = LocalDateTime.now()
)