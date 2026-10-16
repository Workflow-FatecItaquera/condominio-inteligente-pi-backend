package com.example.condominio.repository

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import com.example.condominio.model.PagamentoRegistrado
import org.springframework.data.jpa.repository.JpaRepository

interface PagamentoRegistradoRepository :
    JpaRepository<PagamentoRegistrado, Long> {

    fun findAllByActiveTrue(): List<PagamentoRegistrado>

    fun findByIdAndActiveTrue(id: Long): PagamentoRegistrado?

    @Query(
        """
        SELECT p
        FROM PagamentoRegistrado p
        WHERE p.active = true
        AND LOWER(p.formaPagamento) LIKE LOWER(CONCAT('%', :formaPagamento, '%'))
        """
    )
    fun buscarPorFormaPagamento(
        @Param("formaPagamento") formaPagamento: String
    ): List<PagamentoRegistrado>
}