package com.example.condominio.repository

import com.example.condominio.model.Encomenda
import org.springframework.data.jpa.repository.JpaRepository

interface EncomendaRepository : JpaRepository<Encomenda, Long> {
    fun findAllByActiveTrue(): List<Encomenda>

    fun findByIdAndActiveTrue(id: Long): Encomenda?
}
