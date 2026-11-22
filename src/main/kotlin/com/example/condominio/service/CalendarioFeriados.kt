package com.example.condominio.service

import com.example.condominio.repository.FeriadoRepository
import org.springframework.stereotype.Component
import java.time.LocalDate

/** Consulta de feriados usada pela RN02. Nos testes de unidade é substituída por um stub. */
interface CalendarioFeriados {
    fun ehFeriado(data: LocalDate): Boolean
}

@Component
class CalendarioFeriadosJpa(
    private val repository: FeriadoRepository
) : CalendarioFeriados {

    override fun ehFeriado(data: LocalDate): Boolean {
        return repository.existsByData(data)
    }
}
