package com.example.condominio.repository

import com.example.condominio.model.PagamentoRegistrado
import org.springframework.data.jpa.repository.JpaRepository

interface PagamentoRegistradoRepository :
    JpaRepository<PagamentoRegistrado, Long> {

    fun findAllByActiveTrue(): List<PagamentoRegistrado>

    fun findByIdAndActiveTrue(id: Long): PagamentoRegistrado?
}