package com.example.condominio.repository

import com.example.condominio.model.AreaComum
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface AreaComumRepository : JpaRepository<AreaComum, Long> {

    fun findAllByActiveTrue(): List<AreaComum>

    fun findByIdAndActiveTrue(id: Long): AreaComum?

    @Query(
        """
        SELECT a
        FROM AreaComum a
        WHERE a.active = true
        AND LOWER(a.nome) LIKE LOWER(CONCAT('%', :nome, '%'))
        """
    )
    fun buscarPorNome(
        @Param("nome") nome: String
    ): List<AreaComum>
}