package com.example.condominio.service

import com.example.condominio.model.Condominio
import com.example.condominio.repository.CondominioRepository
import org.springframework.stereotype.Service

@Service
class CondominioService(
    private val repository: CondominioRepository
) {

    fun lerTodos(): List<Condominio> {
        return repository.findAllByActiveTrue()
    }

    fun lerPorId(id: Long): Condominio? {
        return repository.findById(id).orElse(null)
    }

    fun lerPorNome(nome: String): List<Condominio> {
        return repository.findByName(nome)
    }

    fun cadastrar(condominio: Condominio) {
        repository.save(condominio)
    }

    fun apagar(id: Long): Boolean {
        val condominio = repository.findByIdAndActiveTrue(id) ?: return false
        repository.save(condominio.copy(active = false))
        return true
    }

    fun atualizar(id: Long, condominio: Condominio): Boolean {
        return if (repository.existsById(id)) {
            repository.save(condominio.copy(id = id))
            true
        } else {
            false
        }
    }
}