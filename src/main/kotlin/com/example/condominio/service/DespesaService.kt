package com.example.condominio.service

import com.example.condominio.model.Despesa
import com.example.condominio.repository.DespesaRepository
import org.springframework.stereotype.Service

@Service
class DespesaService(
    private val repository: DespesaRepository
) {

    fun lerTodos(): List<Despesa> {
        return repository.findAllByActiveTrue()
    }

    fun lerPorId(id: Long): Despesa? {
        return repository.findByIdAndActiveTrue(id) 
    }

    fun cadastrar(despesa: Despesa) {
        repository.save(despesa)
    }

    fun apagar(id: Long): Boolean {
        val despesa = repository.findByIdAndActiveTrue(id) ?: return false

        repository.save(despesa.copy(active = false))
        return true
    }

    fun atualizar(
        id: Long,
        despesa: Despesa
    ): Boolean {
        val existente = repository.findByIdAndActiveTrue(id)
            ?: return false

        repository.save(
            despesa.copy(
                id = id,
                active = existente.active
            )
        )

        return true
    }
}