package com.example.condominio.mapper

import com.example.condominio.dto.CondominioDTORequest
import com.example.condominio.dto.CondominioDTOResponse
import com.example.condominio.model.Condominio
import org.springframework.stereotype.Component

@Component
class CondominioMapper {

    fun toModel(condominioDto: CondominioDTORequest): Condominio {
        return Condominio(
            id = null,
            nome = condominioDto.nome,
            rua = condominioDto.rua,
            numero = condominioDto.numero,
            bairro = condominioDto.bairro,
            cidade = condominioDto.cidade,
            cnpj = condominioDto.cnpj,
            foto = condominioDto.foto,
            active = condominioDto.active
        )
    }

    fun toResponse(condominio: Condominio): CondominioDTOResponse {
        return CondominioDTOResponse(
            id = condominio.id ?: 0,
            nome = condominio.nome,
            rua = condominio.rua,
            numero = condominio.numero,
            bairro = condominio.bairro,
            cidade = condominio.cidade,
            cnpj = condominio.cnpj,
            foto = condominio.foto,
            active = condominio.active
        )
    }
}