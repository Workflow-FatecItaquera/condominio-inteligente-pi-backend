package com.example.condominio.repository

import com.example.condominio.model.AutorizacaoAcesso
import org.springframework.data.jpa.repository.JpaRepository

interface AutorizacaoAcessoRepository : JpaRepository<AutorizacaoAcesso, Long> {

    fun findAllByVisitanteIdAndUnidadeId(visitanteId: Long, unidadeId: Long): List<AutorizacaoAcesso>

    fun findAllByVisitanteId(visitanteId: Long): List<AutorizacaoAcesso>

    fun findAllByUnidadeId(unidadeId: Long): List<AutorizacaoAcesso>
}
