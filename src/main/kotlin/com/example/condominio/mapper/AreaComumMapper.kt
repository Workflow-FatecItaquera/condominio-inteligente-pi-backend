package com.example.condominio.mapper

import com.example.condominio.dto.AreaComumDTORequest
import com.example.condominio.dto.AreaComumDTOResponse
import com.example.condominio.model.AreaComum
import com.example.condominio.model.Condominio
import org.springframework.stereotype.Component

@Component
class AreaComumMapper {

    fun toModel(areaComumDto: AreaComumDTORequest): AreaComum {
        return AreaComum(
            id = null,
            condominio = Condominio(id = areaComumDto.condominioId),
            nome = areaComumDto.nome,
            tipo = areaComumDto.tipo,
            capacidade = areaComumDto.capacidade,
            active = areaComumDto.active,
            foto = areaComumDto.foto
        )
    }


    fun toResponse(areaComum: AreaComum): AreaComumDTOResponse {
        return AreaComumDTOResponse(
            id = areaComum.id ?: 0,
            condominioId = areaComum.condominio.id ?: 0,
            nome = areaComum.nome,
            tipo = areaComum.tipo,
            capacidade = areaComum.capacidade,
            active = areaComum.active,
            foto = areaComum.foto
        )
    }
}