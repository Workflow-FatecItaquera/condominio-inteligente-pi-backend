package com.example.condominio.model

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDate

@Entity
@Table(name = "cobrancas")
data class CobrancaCondominial(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "apartamento_id", nullable = false)
    val apartamento: Apartamento = Apartamento(),

    @Column(length = 7, nullable = false)
    val competencia: String = "",

    @Column(precision = 10, scale = 2, nullable = false)
    val valor: BigDecimal = BigDecimal.ZERO,

    @Column(nullable = false)
    val vencimento: LocalDate = LocalDate.now(),

    @Column(nullable = false)
    val active: Boolean = true
)