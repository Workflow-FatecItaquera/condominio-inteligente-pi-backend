package com.example.condominio.repository

import com.example.condominio.model.ListaAutorizados
import org.springframework.data.jpa.repository.JpaRepository

interface ListaAutorizadosRepository : JpaRepository<ListaAutorizados, Long> {

    fun findFirstByUnidadeIdAndActiveTrueOrderByAtualizadaEmDescIdDesc(unidadeId: Long): ListaAutorizados?
}
