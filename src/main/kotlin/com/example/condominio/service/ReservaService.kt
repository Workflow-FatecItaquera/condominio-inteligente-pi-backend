package com.example.condominio.service

import com.example.condominio.model.Reserva
import com.example.condominio.repository.ReservaRepository
import org.springframework.stereotype.Service

@Service
class ReservaService(
    private val repository: ReservaRepository
) {

    fun lerTodos(): List<Reserva> {
        return repository.findAll()
    }

    fun lerPorId(id: Long): Reserva? {
        return repository.findById(id).orElse(null)
    }

    fun cadastrar(reserva: Reserva) {
        repository.save(reserva)
    }

    fun apagar(id: Long): Boolean {
        return if (repository.existsById(id)) {
            repository.deleteById(id)
            true
        } else {
            false
        }
    }

    fun atualizar(id: Long, reserva: Reserva): Boolean {
        return if (repository.existsById(id)) {
            repository.save(reserva.copy(id = id))
            true
        } else {
            false
        }
    }
}