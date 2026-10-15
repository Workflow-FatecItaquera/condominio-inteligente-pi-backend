package com.example.condominio.repository

import com.example.condominio.model.Despesa
import org.springframework.data.jpa.repository.JpaRepository

interface DespesaRepository : JpaRepository<Despesa, Long> {

    fun findAllByActiveTrue(): List<Despesa>

    fun findByIdAndActiveTrue(id: Long): Despesa?
}