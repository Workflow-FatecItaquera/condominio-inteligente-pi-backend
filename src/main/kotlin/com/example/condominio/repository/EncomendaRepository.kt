package com.example.condominio.repository

import com.example.condominio.model.Encomenda
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface EncomendaRepository : JpaRepository<Encomenda, Long> {
    fun findAllByActiveTrue(): List<Encomenda>

    fun findByIdAndActiveTrue(id: Long): Encomenda?

    @Query(
        """
        SELECT e
        FROM Encomenda e
        WHERE e.active = true
        AND LOWER(e.codigo) LIKE LOWER(CONCAT('%', :codigo, '%'))
        """
    )
    fun buscarPorCodigo(
        @Param("codigo") codigo: String
    ): List<Encomenda>
}
