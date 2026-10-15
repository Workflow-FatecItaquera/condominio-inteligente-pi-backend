package com.example.condominio.service

import com.example.condominio.model.Ocorrencia
import com.example.condominio.repository.OcorrenciaRepository
import org.springframework.stereotype.Service

@Service
class OcorrenciaService(
    private val repository: OcorrenciaRepository
) {

    fun lerTodos(): List<Ocorrencia> {
        return repository.findAllByActiveTrue()
    }

    fun lerPorId(id: Long): Ocorrencia? {
        return repository.findByIdAndActiveTrue(id)
    }

    fun cadastrar(ocorrencia: Ocorrencia) {
        repository.save(ocorrencia)
    }

    fun apagar(id: Long): Boolean {
        val ocorrencia = repository.findByIdAndActiveTrue(id) ?: return false
        repository.save(ocorrencia.copy(active = false))
        return true
    }

    fun atualizar(id: Long, ocorrencia: Ocorrencia): Boolean {
        val existente = repository.findByIdAndActiveTrue(id) ?: return false
        repository.save(
            ocorrencia.copy(
                id = id,
                criadaEm = existente.criadaEm,
                active = existente.active
            )
        )
        return true
    }
}
