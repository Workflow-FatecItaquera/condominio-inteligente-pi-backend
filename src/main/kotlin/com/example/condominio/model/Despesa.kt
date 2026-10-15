package com.example.condominio.model

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDate

@Entity
@Table(name = "despesas")
data class Despesa(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "condominio_id", nullable = false)
    val condominio: Condominio = Condominio(),

    @Column(length = 255, nullable = false)
    val descricao: String = "",

    @Column(length = 50, nullable = false)
    val categoria: String = "",

    @Column(precision = 10, scale = 2, nullable = false)
    val valor: BigDecimal = BigDecimal.ZERO,

    @Column(nullable = false)
    val vencimento: LocalDate = LocalDate.now(),

    val dataPagamento: LocalDate? = null,

    @Column(nullable = false)
    val active: Boolean = true
)