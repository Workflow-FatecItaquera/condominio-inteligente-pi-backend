package com.example.condominio.model

import jakarta.persistence.*

@Entity
@Table(name = "areascomuns")
data class AreaComum(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "condominio_id", nullable = false)
    val condominio: Condominio = Condominio(),

    @Column(length = 100, nullable = false)
    val nome: String = "",

    @Column(length = 50, nullable = false)
    val tipo: String = "",

    @Column(nullable = false)
    val capacidade: Int = 0,

    @Column(nullable = false)
    val active: Boolean = true,

    @Column(length = 255)
    val foto: String? = null
)