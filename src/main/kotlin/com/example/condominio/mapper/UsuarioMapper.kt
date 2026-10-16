package com.example.condominio.mapper

import com.example.condominio.dto.UsuarioDTORequest
import com.example.condominio.dto.UsuarioDTOResponse
import com.example.condominio.model.Usuario
import org.springframework.stereotype.Component

@Component
class UsuarioMapper {

    fun toModel(usuarioDto: UsuarioDTORequest): Usuario {
        return Usuario(
            id = null,
            nome = usuarioDto.nome,
            cpf = usuarioDto.cpf,
            telefone = usuarioDto.telefone,
            foto = usuarioDto.foto
        )
    }

    fun toResponse(usuario: Usuario): UsuarioDTOResponse {
        return UsuarioDTOResponse(
            id = usuario.id ?: 0,
            nome = usuario.nome,
            cpf = usuario.cpf,
            email = usuario.email,
            telefone = usuario.telefone,
            papeis = usuario.papeis,
            foto = usuario.foto,
            active = usuario.active,
            dataRegistro = usuario.dataRegistro
        )
    }
}