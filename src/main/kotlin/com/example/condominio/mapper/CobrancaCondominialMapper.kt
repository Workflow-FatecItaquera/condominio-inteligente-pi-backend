package com.example.condominio.mapper

import com.example.condominio.dto.CobrancaCondominialDTORequest
import com.example.condominio.dto.CobrancaCondominialDTOResponse
import com.example.condominio.model.Apartamento
import com.example.condominio.model.CobrancaCondominial
import org.springframework.stereotype.Component

@Component
class CobrancaCondominialMapper {

    fun toModel(cobrancaDto: CobrancaCondominialDTORequest): CobrancaCondominial {
        return CobrancaCondominial(
            id = null,
            apartamento = Apartamento(id = cobrancaDto.apartamentoId),
            competencia = cobrancaDto.competencia,
            valor = cobrancaDto.valor,
            vencimento = cobrancaDto.vencimento,
        )
    }

    fun toResponse(cobranca: CobrancaCondominial): CobrancaCondominialDTOResponse {
        return CobrancaCondominialDTOResponse(
            id = cobranca.id ?: 0,
            apartamentoId = cobranca.apartamento.id ?: 0,
            competencia = cobranca.competencia,
            valor = cobranca.valor,
            vencimento = cobranca.vencimento,
            active = cobranca.active
        )
    }
}