package com.example.condominio.mapper

import com.example.condominio.dto.EncomendaDTORequest
import com.example.condominio.dto.EncomendaDTOResponse
import com.example.condominio.model.Apartamento
import com.example.condominio.model.Encomenda
import org.springframework.stereotype.Component

@Component
class EncomendaMapper {

    fun toModel(encomendaDto: EncomendaDTORequest): Encomenda {
        return Encomenda(
            id = null,
            apartamento = Apartamento(id = encomendaDto.apartamentoId),
            descricao = encomendaDto.descricao,
            codigo = encomendaDto.codigo,
            recebidaEm = encomendaDto.recebidaEm,
            retiradaEm = encomendaDto.retiradaEm,
            status = encomendaDto.status
        )
    }

    fun toResponse(encomenda: Encomenda): EncomendaDTOResponse {
        return EncomendaDTOResponse(
            id = encomenda.id ?: 0,
            apartamentoId = encomenda.apartamento.id ?: 0,
            descricao = encomenda.descricao,
            codigo = encomenda.codigo,
            recebidaEm = encomenda.recebidaEm,
            retiradaEm = encomenda.retiradaEm,
            status = encomenda.status,
            active = encomenda.active
        )
    }
}
