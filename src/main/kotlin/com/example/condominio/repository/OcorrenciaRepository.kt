package com.example.condominio.repository

import com.example.condominio.model.Ocorrencia
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface OcorrenciaRepository : JpaRepository<Ocorrencia, Long> {
    fun findAllByActiveTrue(): List<Ocorrencia>

    fun findByIdAndActiveTrue(id: Long): Ocorrencia?

    @Query(
        """
        SELECT o
        FROM Ocorrencia o
        WHERE o.active = true
        AND LOWER(o.categoria) LIKE LOWER(CONCAT('%', :categoria, '%'))
        """
    )
    fun buscarPorCategoria(
        @Param("categoria") categoria: String
    ): List<Ocorrencia>
}
