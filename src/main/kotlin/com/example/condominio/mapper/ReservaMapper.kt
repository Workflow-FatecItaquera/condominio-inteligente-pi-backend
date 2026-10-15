package com.example.condominio.mapper

import com.example.condominio.dto.ReservaDTORequest
import com.example.condominio.dto.ReservaDTOResponse
import com.example.condominio.model.AreaComum
import com.example.condominio.model.Reserva
import com.example.condominio.model.Usuario
import org.springframework.stereotype.Component

@Component
class ReservaMapper {

    fun toModel(reservaDto: ReservaDTORequest): Reserva {
        return Reserva(
            id = null,
            usuario = Usuario(id = reservaDto.usuarioId),
            areaComum = AreaComum(id = reservaDto.areaComumId),
            inicio = reservaDto.inicio,
            fim = reservaDto.fim,
            motivo = reservaDto.motivo,
            active = reservaDto.active
        )
    }

    fun toResponse(reserva: Reserva): ReservaDTOResponse {
        return ReservaDTOResponse(
            id = reserva.id ?: 0,
            usuarioId = reserva.usuario.id ?: 0,
            areaComumId = reserva.areaComum.id ?: 0,
            inicio = reserva.inicio,
            fim = reserva.fim,
            motivo = reserva.motivo,
            active = reserva.active
        )
    }
}