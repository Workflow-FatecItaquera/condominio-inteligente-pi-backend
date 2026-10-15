package com.example.condominio.mapper

import com.example.condominio.dto.PagamentoRegistradoDTORequest
import com.example.condominio.dto.PagamentoRegistradoDTOResponse
import com.example.condominio.model.CobrancaCondominial
import com.example.condominio.model.PagamentoRegistrado
import com.example.condominio.model.Usuario
import org.springframework.stereotype.Component

@Component
class PagamentoRegistradoMapper {

    fun toModel(pagamentoDto: PagamentoRegistradoDTORequest): PagamentoRegistrado {
        return PagamentoRegistrado(
            id = null,
            cobranca = CobrancaCondominial(id = pagamentoDto.cobrancaId),
            valor = pagamentoDto.valor,
            dataPagamento = pagamentoDto.dataPagamento,
            formaPagamento = pagamentoDto.formaPagamento,
            confirmadoPor = Usuario(id = pagamentoDto.confirmadoPorId)
        )
    }

    fun toResponse(pagamento: PagamentoRegistrado): PagamentoRegistradoDTOResponse {
        return PagamentoRegistradoDTOResponse(
            id = pagamento.id ?: 0,
            cobrancaId = pagamento.cobranca.id ?: 0,
            valor = pagamento.valor,
            dataPagamento = pagamento.dataPagamento,
            formaPagamento = pagamento.formaPagamento,
            confirmadoPorId = pagamento.confirmadoPor.id ?: 0,
            active = pagamento.active
        )
    }
}