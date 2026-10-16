package com.example.condominio.repository

import com.example.condominio.model.Apartamento
import com.example.condominio.model.Usuario
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ApartamentoRepository : JpaRepository<Apartamento, Long> {
    fun findAllByActiveTrue(): List<Apartamento>
    fun findByIdAndActiveTrue(id: Long): Apartamento?

    @Query("SELECT a FROM Apartamento a WHERE a.bloco LIKE %:bloco%")
    fun findByBlock(@Param("bloco") bloco: String): List<Apartamento>
}