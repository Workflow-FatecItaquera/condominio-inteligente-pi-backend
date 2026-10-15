package com.example.condominio.service

import com.example.condominio.model.AreaComum
import com.example.condominio.repository.AreaComumRepository
import org.springframework.stereotype.Service

@Service
class AreaComumService(
    private val repository: AreaComumRepository
) {

    fun lerTodos(): List<AreaComum> {
        return repository.findAll()
    }

    fun lerPorId(id: Long): AreaComum? {
        return repository.findById(id).orElse(null)
    }

    fun cadastrar(areaComum: AreaComum) {
        repository.save(areaComum)
    }

    fun apagar(id: Long): Boolean {
        return if (repository.existsById(id)) {
            repository.deleteById(id)
            true
        } else {
            false
        }
    }

    fun atualizar(id: Long, areaComum: AreaComum): Boolean {
        return if (repository.existsById(id)) {
            repository.save(areaComum.copy(id = id))
            true
        } else {
            false
        }
    }
}