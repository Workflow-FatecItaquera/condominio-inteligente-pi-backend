package com.example.condominio.service

import com.example.condominio.model.Condominio
import com.example.condominio.repository.CondominioRepository
import org.springframework.stereotype.Service

@Service
class CondominioService(
    private val repository: CondominioRepository
) {

    fun lerTodos(): List<Condominio> {
        return repository.findAll()
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
        return if (repository.existsById(id)) {
            repository.deleteById(id)
            true
        } else {
            false
        }
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