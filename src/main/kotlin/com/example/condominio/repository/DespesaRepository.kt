package com.example.condominio.repository

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import com.example.condominio.model.Despesa
import org.springframework.data.jpa.repository.JpaRepository

interface DespesaRepository : JpaRepository<Despesa, Long> {

    fun findAllByActiveTrue(): List<Despesa>

    fun findByIdAndActiveTrue(id: Long): Despesa?

    @Query(
        """
        SELECT d
        FROM Despesa d
        WHERE d.active = true
        AND LOWER(d.categoria) LIKE LOWER(CONCAT('%', :categoria, '%'))
        """
    )
    fun buscarPorCategoria(
        @Param("categoria") categoria: String
    ): List<Despesa>
}