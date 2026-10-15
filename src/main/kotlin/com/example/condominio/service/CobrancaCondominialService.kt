package com.example.condominio.service

import com.example.condominio.model.CobrancaCondominial
import com.example.condominio.repository.CobrancaCondominialRepository
import org.springframework.stereotype.Service

@Service
class CobrancaCondominialService(
    private val repository: CobrancaCondominialRepository
) {

    fun lerTodos(): List<CobrancaCondominial> {
        return repository.findAllByActiveTrue()
    }

    fun lerPorId(id: Long): CobrancaCondominial? {
        return repository.findByIdAndActiveTrue(id)
    }

    fun cadastrar(cobranca: CobrancaCondominial) {
        repository.save(cobranca)
    }

    fun apagar(id: Long): Boolean {
        val cobranca = repository.findByIdAndActiveTrue(id) ?: return false

        repository.save(cobranca.copy(active = false))
        return true
    }

    fun atualizar(
        id: Long,
        cobranca: CobrancaCondominial
    ): Boolean {
        val existente = repository.findByIdAndActiveTrue(id)
            ?: return false

        repository.save(
            cobranca.copy(
                id = id,
                active = existente.active
            )
        )

        return true
    }
}