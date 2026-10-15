package com.example.condominio.repository

import com.example.condominio.model.AreaComum
import org.springframework.data.jpa.repository.JpaRepository

interface AreaComumRepository : JpaRepository<AreaComum, Long> {
}