package com.example.condominio.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "usuarios")
data class Usuario(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, length = 100)
    val nome: String = "",

    @Column(nullable = false, unique = true, length = 11)
    val cpf: String = "",

    @Column(nullable = false, unique = true, length = 200)
    val email: String = "",

    @Column(nullable = false, length = 255)
    val senha: String = "",

    @Column(length = 20)
    val telefone: String? = null,

    @Column(nullable = false)
    //sem legth, campos pre-definidos
    val papeis: String = "MORADOR",

    @Column(length = 255)
    val foto: String? = null,

    @Column(nullable = false)
    val active: Boolean = true,

    @Column(nullable = false)
    val dataRegistro: LocalDateTime = LocalDateTime.now()
)
