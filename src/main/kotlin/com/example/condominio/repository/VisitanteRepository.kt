
package com.example.condominio.repository

import com.example.condominio.model.Visitante
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface VisitanteRepository : JpaRepository<Visitante, Long> {

    fun findAllByActiveTrue(): List<Visitante>

    fun findByIdAndActiveTrue(id: Long): Visitante?

    fun findFirstByDocumento(documento: String): Visitante?

    @Query(
        """
        SELECT v
        FROM Visitante v
        WHERE v.active = true
        AND LOWER(v.nome) LIKE LOWER(CONCAT('%', :nome, '%'))
        """
    )
    fun buscarPorNome(
        @Param("nome") nome: String
    ): List<Visitante>
}
