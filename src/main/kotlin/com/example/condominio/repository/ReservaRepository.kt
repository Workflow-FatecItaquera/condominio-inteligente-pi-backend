package com.example.condominio.repository

import com.example.condominio.model.Reserva
import org.springframework.data.jpa.repository.JpaRepository

interface ReservaRepository : JpaRepository<Reserva, Long> {
}