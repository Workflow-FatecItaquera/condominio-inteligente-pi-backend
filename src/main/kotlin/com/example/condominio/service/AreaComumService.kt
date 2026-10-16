package com.example.condominio.service

import com.example.condominio.model.AreaComum
import com.example.condominio.repository.AreaComumRepository
import org.springframework.stereotype.Service

@Service
class AreaComumService(
    private val repository: AreaComumRepository
) {

    fun lerTodos(): List<AreaComum> {
        return repository.findAllByActiveTrue()
    }

    fun lerPorId(id: Long): AreaComum? {
        return repository.findByIdAndActiveTrue(id)
    }

    fun cadastrar(areaComum: AreaComum) {
        repository.save(areaComum)
    }

    fun apagar(id: Long): Boolean {
        val areaComum = repository.findByIdAndActiveTrue(id) ?: return false

        repository.save(areaComum.copy(active = false))
        return true
    }

    fun atualizar(id: Long, areaComum: AreaComum): Boolean {
        val existente = repository.findByIdAndActiveTrue(id)
            ?: return false

        repository.save(
            areaComum.copy(
                id = id,
                active = existente.active
            )
        )

        return true
    }

    fun buscarPorNome(nome: String): List<AreaComum> {
    return repository.buscarPorNome(nome)
    }
}