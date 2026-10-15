package com.example.condominio.mapper

import com.example.condominio.dto.DespesaDTORequest
import com.example.condominio.dto.DespesaDTOResponse
import com.example.condominio.model.Condominio
import com.example.condominio.model.Despesa
import org.springframework.stereotype.Component

@Component
class DespesaMapper {

    fun toModel(despesaDto: DespesaDTORequest): Despesa {
        return Despesa(
            id = null,
            condominio = Condominio(id = despesaDto.condominioId),
            descricao = despesaDto.descricao,
            categoria = despesaDto.categoria,
            valor = despesaDto.valor,
            vencimento = despesaDto.vencimento,
            dataPagamento = despesaDto.dataPagamento,
        )
    }

    fun toResponse(despesa: Despesa): DespesaDTOResponse {
        return DespesaDTOResponse(
            id = despesa.id ?: 0,
            condominioId = despesa.condominio.id ?: 0,
            descricao = despesa.descricao,
            categoria = despesa.categoria,
            valor = despesa.valor,
            vencimento = despesa.vencimento,
            dataPagamento = despesa.dataPagamento,
            active = despesa.active
        )
    }
}