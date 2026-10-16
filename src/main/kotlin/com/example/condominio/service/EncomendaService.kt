package com.example.condominio.service

import com.example.condominio.model.Encomenda
import com.example.condominio.repository.EncomendaRepository
import org.springframework.stereotype.Service

@Service
class EncomendaService(
    private val repository: EncomendaRepository
) {

    fun lerTodos(): List<Encomenda> {
        return repository.findAllByActiveTrue()
    }

    fun lerPorId(id: Long): Encomenda? {
        return repository.findByIdAndActiveTrue(id)
    }

    fun cadastrar(encomenda: Encomenda) {
        repository.save(encomenda)
    }

    fun apagar(id: Long): Boolean {
        val encomenda = repository.findByIdAndActiveTrue(id) ?: return false
        repository.save(encomenda.copy(active = false))
        return true
    }

    fun atualizar(id: Long, encomenda: Encomenda): Boolean {
        val existente = repository.findByIdAndActiveTrue(id) ?: return false
        repository.save(
            encomenda.copy(
                id = id,
                active = existente.active
            )
        )
        return true
    }

    fun buscaPorCodigo(codigo: String): List<Encomenda> {
        return repository.buscarPorCodigo(codigo.trim())
    }
}
