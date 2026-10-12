package com.example.condominio.model

import jakarta.persistence.*

@Entity
@Table(name = "condominios")
data class Condominio(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, length = 100)
    val nome: String = "",

    @Column(nullable = false, length = 80)
    val rua: String = "",

    @Column(nullable = false)
    val numero: Int = 0,

    @Column(nullable = false, length = 80)
    val bairro: String = "",

    @Column(nullable = false, length = 80)
    val cidade: String = "",

    @Column(nullable = false, unique = true, length = 14)
    val cnpj: String = "",

    @Column(length = 255)
    val foto: String? = null,

    @Column(nullable = false)
    val active: Boolean = true
)