package com.example.condominio.service

import com.example.condominio.model.Apartamento
import com.example.condominio.model.Condominio
import com.example.condominio.repository.ApartamentoRepository
import org.springframework.stereotype.Service

@Service
class ApartamentoService(
    private val repository: ApartamentoRepository
) {

    fun lerTodos(): List<Apartamento> {
        return repository.findAllByActiveTrue()
    }

    fun lerPorId(id: Long): Apartamento? {
        return repository.findByIdAndActiveTrue(id)
    }

    fun lerPorBloco(bloco: String): List<Apartamento> {
        return repository.findByBlock(bloco)
    }

    fun cadastrar(apartamento: Apartamento) {
        repository.save(apartamento)
    }

    fun apagar(id: Long): Boolean {
        val apartamento = repository.findByIdAndActiveTrue(id) ?: return false
        repository.save(apartamento.copy(active = false))
        return true
    }

    fun atualizar(id: Long, apartamento: Apartamento): Boolean {
        return if (repository.existsById(id)) {
            repository.save(apartamento.copy(id = id))
            true
        } else {
            false
        }
    }
}