package com.example.condominio.repository

import com.example.condominio.model.Visitante
import org.springframework.data.jpa.repository.JpaRepository

interface VisitanteRepository : JpaRepository<Visitante, Long> {
}