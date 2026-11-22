package com.example.condominio.repository

import com.example.condominio.model.Feriado
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate

interface FeriadoRepository : JpaRepository<Feriado, Long> {

    fun existsByData(data: LocalDate): Boolean
}
