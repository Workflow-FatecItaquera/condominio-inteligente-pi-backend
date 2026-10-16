package com.example.condominio.mapper

import com.example.condominio.dto.ApartamentoDTORequest
import com.example.condominio.dto.ApartamentoDTOResponse
import com.example.condominio.model.Apartamento
import com.example.condominio.model.Condominio
import org.springframework.stereotype.Component

@Component
class ApartamentoMapper {

    fun toModel(apartamentoDto: ApartamentoDTORequest): Apartamento {
        return Apartamento(
            id = null,
            condominio = Condominio(id = apartamentoDto.condominioId),
            bloco = apartamentoDto.bloco,
            numero = apartamentoDto.numero,
            andar = apartamentoDto.andar,
            interfone = apartamentoDto.interfone,
            active = apartamentoDto.active
        )
    }

    fun toResponse(apartamento: Apartamento): ApartamentoDTOResponse {
        return ApartamentoDTOResponse(
            id = apartamento.id ?: 0,
            condominioId = apartamento.condominio.id ?: 0,
            bloco = apartamento.bloco,
            numero = apartamento.numero,
            andar = apartamento.andar,
            interfone = apartamento.interfone,
            active = apartamento.active
        )
    }
}