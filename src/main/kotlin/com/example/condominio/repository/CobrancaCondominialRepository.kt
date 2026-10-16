package com.example.condominio.repository

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import com.example.condominio.model.CobrancaCondominial
import org.springframework.data.jpa.repository.JpaRepository

interface CobrancaCondominialRepository :
    JpaRepository<CobrancaCondominial, Long> {

    fun findAllByActiveTrue(): List<CobrancaCondominial>

    fun findByIdAndActiveTrue(id: Long): CobrancaCondominial?

    @Query(
        """
        SELECT c
        FROM CobrancaCondominial c
        WHERE c.active = true
        AND LOWER(c.descricao) LIKE LOWER(CONCAT('%', :descricao, '%'))
        """
    )
    
    fun buscarPorDescricao(
        @Param("descricao") descricao: String
    ): List<CobrancaCondominial>
}