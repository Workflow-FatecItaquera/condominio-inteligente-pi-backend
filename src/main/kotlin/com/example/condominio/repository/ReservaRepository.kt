
package com.example.condominio.repository

import com.example.condominio.model.Reserva
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ReservaRepository : JpaRepository<Reserva, Long> {

    fun findAllByActiveTrue(): List<Reserva>

    fun findByIdAndActiveTrue(id: Long): Reserva?

    @Query(
    """
    SELECT r
    FROM Reserva r
    WHERE r.active = true
    AND LOWER(r.motivo) LIKE LOWER(CONCAT('%', :motivo, '%'))
    """
    )
    
    fun buscarPorMotivo(
        @Param("motivo") motivo: String
    ): List<Reserva>
}
