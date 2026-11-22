package com.example.condominio.repository

import com.example.condominio.model.RegistroAcesso
import org.springframework.data.jpa.repository.JpaRepository

interface RegistroAcessoRepository : JpaRepository<RegistroAcesso, Long> {

    fun findAllBySaidaEmIsNull(): List<RegistroAcesso>

    fun findAllByUnidadeId(unidadeId: Long): List<RegistroAcesso>
}
