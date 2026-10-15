package com.example.condominio.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "reservas")
data class Reserva(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    val usuario: Usuario = Usuario(),

    @ManyToOne
    @JoinColumn(name = "area_comum_id", nullable = false)
    val areaComum: AreaComum = AreaComum(),

    @Column(nullable = false)
    val inicio: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    val fim: LocalDateTime = LocalDateTime.now(),

    @Column(length = 30, nullable = false)
    val motivo: String = "",

   @Column(nullable = false)
    val active: Boolean = true,
)