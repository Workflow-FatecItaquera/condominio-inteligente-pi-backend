package com.example.condominio.repository

import com.example.condominio.model.Apartamento
import com.example.condominio.model.Usuario
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface UsuarioRepository : JpaRepository<Usuario, Long> {
    fun findByEmail(email: String): Usuario?
    fun findByCpf(cpf: String): Usuario?

    @Query("SELECT u FROM Usuario u WHERE u.nome LIKE %:nome%")
    fun findByName(@Param("nome") nome: String): List<Usuario>
}