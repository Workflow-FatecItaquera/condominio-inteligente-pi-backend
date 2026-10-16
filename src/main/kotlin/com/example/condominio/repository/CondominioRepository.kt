package com.example.condominio.repository

import com.example.condominio.model.Apartamento
import com.example.condominio.model.Condominio
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface CondominioRepository : JpaRepository<Condominio, Long> {
    fun findAllByActiveTrue(): List<Condominio>
    fun findByIdAndActiveTrue(id: Long): Condominio?

    @Query("SELECT c FROM Condominio c WHERE c.nome LIKE %:nome%")
    fun findByName(@Param("nome") nome: String): List<Condominio>
}