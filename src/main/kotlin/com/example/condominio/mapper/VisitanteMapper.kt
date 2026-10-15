package com.example.condominio.mapper

import com.example.condominio.dto.VisitanteDTORequest
import com.example.condominio.dto.VisitanteDTOResponse
import com.example.condominio.model.Visitante
import org.springframework.stereotype.Component

@Component
class VisitanteMapper {

    fun toModel(visitanteDto: VisitanteDTORequest): Visitante {
        return Visitante(
            id = null,
            nome = visitanteDto.nome,
            visitaAutorizada = visitanteDto.visitaAutorizada,
            ultimaVisita = visitanteDto.ultimaVisita,
            documento = visitanteDto.documento,
            telefone = visitanteDto.telefone,
            email = visitanteDto.email,
            foto = visitanteDto.foto,
            dataRegistro = visitanteDto.dataRegistro ?: java.time.LocalDateTime.now()
        )
    }

    fun toResponse(visitante: Visitante): VisitanteDTOResponse {
        return VisitanteDTOResponse(
            id = visitante.id ?: 0,
            nome = visitante.nome,
            visitaAutorizada = visitante.visitaAutorizada,
            ultimaVisita = visitante.ultimaVisita,
            documento = visitante.documento,
            telefone = visitante.telefone,
            email = visitante.email,
            foto = visitante.foto,
    dataRegistro = visitante.dataRegistro ?: java.time.LocalDateTime.now()        )
    }
}