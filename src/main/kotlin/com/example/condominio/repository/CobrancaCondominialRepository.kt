package com.example.condominio.repository

import com.example.condominio.model.CobrancaCondominial
import org.springframework.data.jpa.repository.JpaRepository

interface CobrancaCondominialRepository :
    JpaRepository<CobrancaCondominial, Long> {

    fun findAllByActiveTrue(): List<CobrancaCondominial>

    fun findByIdAndActiveTrue(id: Long): CobrancaCondominial?
}