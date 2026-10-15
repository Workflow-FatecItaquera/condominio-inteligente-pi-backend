package com.example.condominio.model

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDate

@Entity
@Table(name = "pagamentos")
data class PagamentoRegistrado(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "cobranca_id", nullable = false)
    val cobranca: CobrancaCondominial = CobrancaCondominial(),

    @Column(precision = 10, scale = 2, nullable = false)
    val valor: BigDecimal = BigDecimal.ZERO,

    @Column(nullable = false)
    val dataPagamento: LocalDate = LocalDate.now(),

    @Column(length = 30, nullable = false)
    val formaPagamento: String = "",

    @ManyToOne
    @JoinColumn(name = "confirmado_por_id", nullable = false)
    val confirmadoPor: Usuario = Usuario(),

    @Column(nullable = false)
    val active: Boolean = true
)