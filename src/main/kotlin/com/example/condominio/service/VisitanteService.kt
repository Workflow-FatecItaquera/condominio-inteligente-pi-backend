package com.example.condominio.service

import com.example.condominio.model.Visitante
import com.example.condominio.repository.VisitanteRepository
import org.springframework.stereotype.Service

@Service
class VisitanteService(
    private val repository: VisitanteRepository
) {

    fun lerTodos(): List<Visitante> {
        return repository.findAll()
    }

    fun lerPorId(id: Long): Visitante? {
        return repository.findById(id).orElse(null)
    }

    fun cadastrar(visitante: Visitante) {
        repository.save(visitante)
    }

    fun apagar(id: Long): Boolean {
        return if (repository.existsById(id)) {
            repository.deleteById(id)
            true
        } else {
            false
        }
    }

    fun atualizar(id: Long, visitante: Visitante): Boolean {
        return if (repository.existsById(id)) {
            repository.save(visitante.copy(id = id))
            true
        } else {
            false
        }
    }
}