package com.example.condominio.model

import jakarta.persistence.*
import java.time.LocalDate

/** Feriado cadastrado, consultado pela RN02 (feriado exige autorização expressa). */
@Entity
@Table(name = "feriados")
data class Feriado(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "data_feriado", nullable = false, unique = true)
    val data: LocalDate = LocalDate.now(),

    @Column(nullable = false, length = 100)
    val nome: String = ""
)
