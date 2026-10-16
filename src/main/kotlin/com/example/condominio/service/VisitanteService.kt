
package com.example.condominio.service

import com.example.condominio.model.Visitante
import com.example.condominio.repository.VisitanteRepository
import org.springframework.stereotype.Service

@Service
class VisitanteService(
    private val repository: VisitanteRepository
) {

    fun lerTodos(): List<Visitante> {
        return repository.findAllByActiveTrue()
    }

    fun lerPorId(id: Long): Visitante? {
        return repository.findByIdAndActiveTrue(id)
    }

    fun cadastrar(visitante: Visitante) {
        repository.save(visitante)
    }

    fun apagar(id: Long): Boolean {
        val visitante = repository.findByIdAndActiveTrue(id) ?: return false

        repository.save(visitante.copy(active = false))
        return true
    }

    fun atualizar(id: Long, visitante: Visitante): Boolean {
        val existente = repository.findByIdAndActiveTrue(id)
            ?: return false

        repository.save(
            visitante.copy(
                id = id,
                active = existente.active
            )
        )

        return true
    }
}
