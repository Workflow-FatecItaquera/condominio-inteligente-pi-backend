package com.example.condominio.model

import jakarta.persistence.*

@Entity
@Table(name = "apartamentos")
data class Apartamento(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "condominio_id", nullable = false)
    val condominio: Condominio = Condominio(),

    @Column(nullable = false, length = 10)
    val bloco: String = "",

    @Column(nullable = false)
    val numero: Int = 0,

    @Column(nullable = false, length = 10)
    val andar: String = "",

    @Column(nullable = false)
    val active: Boolean = true
)