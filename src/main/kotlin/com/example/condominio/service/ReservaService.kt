package com.example.condominio.service

import com.example.condominio.model.Reserva
import com.example.condominio.repository.ReservaRepository
import org.springframework.stereotype.Service

@Service
class ReservaService(
    private val repository: ReservaRepository
) {

    fun lerTodos(): List<Reserva> {
        return repository.findAllByActiveTrue()
    }

    fun lerPorId(id: Long): Reserva? {
        return repository.findByIdAndActiveTrue(id)
    }

    fun cadastrar(reserva: Reserva) {
        repository.save(reserva)
    }

    fun apagar(id: Long): Boolean {
        val reserva = repository.findByIdAndActiveTrue(id) ?: return false

        repository.save(reserva.copy(active = false))
        return true
    }

    fun atualizar(id: Long, reserva: Reserva): Boolean {
        val existente = repository.findByIdAndActiveTrue(id)
            ?: return false

        repository.save(
            reserva.copy(
                id = id,
                active = existente.active
            )
        )

        return true
    }

    fun buscarPorMotivo(motivo: String): List<Reserva> {
    return repository.buscarPorMotivo(motivo)
    }
}