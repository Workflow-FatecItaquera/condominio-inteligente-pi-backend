package com.example.condominio.service

import com.example.condominio.model.PagamentoRegistrado
import com.example.condominio.repository.PagamentoRegistradoRepository
import org.springframework.stereotype.Service

@Service
class PagamentoRegistradoService(
    private val repository: PagamentoRegistradoRepository
) {

    fun lerTodos(): List<PagamentoRegistrado> {
        return repository.findAllByActiveTrue()
    }

    fun lerPorId(id: Long): PagamentoRegistrado? {
        return repository.findByIdAndActiveTrue(id)
    }

    fun cadastrar(pagamento: PagamentoRegistrado) {
        repository.save(pagamento)
    }

    fun apagar(id: Long): Boolean {
        val pagamento = repository.findByIdAndActiveTrue(id) ?: return false

        repository.save(pagamento.copy(active = false))
        return true
    }

    fun atualizar(
        id: Long,
        pagamento: PagamentoRegistrado
    ): Boolean {
        val existente = repository.findByIdAndActiveTrue(id)
            ?: return false

        repository.save(
            pagamento.copy(
                id = id,
                active = existente.active
            )
        )

        return true
    }
}