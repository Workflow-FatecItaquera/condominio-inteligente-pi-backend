package com.example.condominio.mapper

import com.example.condominio.dto.OcorrenciaDTORequest
import com.example.condominio.dto.OcorrenciaDTOResponse
import com.example.condominio.model.Condominio
import com.example.condominio.model.Ocorrencia
import org.springframework.stereotype.Component

@Component
class OcorrenciaMapper {

    fun toModel(ocorrenciaDto: OcorrenciaDTORequest): Ocorrencia {
        return Ocorrencia(
            id = null,
            condominio = Condominio(id = ocorrenciaDto.condominioId),
            descricao = ocorrenciaDto.descricao,
            categoria = ocorrenciaDto.categoria,
            status = ocorrenciaDto.status
        )
    }

    fun toResponse(ocorrencia: Ocorrencia): OcorrenciaDTOResponse {
        return OcorrenciaDTOResponse(
            id = ocorrencia.id ?: 0,
            condominioId = ocorrencia.condominio.id ?: 0,
            descricao = ocorrencia.descricao,
            categoria = ocorrencia.categoria,
            status = ocorrencia.status,
            criadaEm = ocorrencia.criadaEm,
            active = ocorrencia.active
        )
    }
}
