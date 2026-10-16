package com.example.condominio.controller

import com.example.condominio.dto.UsuarioDTORequest
import com.example.condominio.dto.UsuarioDTOResponse
import com.example.condominio.dto.UsuarioPapelDTO
import com.example.condominio.mapper.UsuarioMapper
import com.example.condominio.service.UsuarioService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/usuarios")
class UsuarioController(
    private val service: UsuarioService,
    private val mapper: UsuarioMapper
) {

    @GetMapping
    fun lerTodos(): List<UsuarioDTOResponse> {
        return service.lerTodos().map { mapper.toResponse(it) }
    }

    @GetMapping("/{id}")
    fun lerPorId(@PathVariable("id") id: Long): ResponseEntity<UsuarioDTOResponse> {
        val usuario = service.lerPorId(id)
            ?: return ResponseEntity.status(404).build()
        return ResponseEntity.ok(mapper.toResponse(usuario))
    }

    @GetMapping("/busca/{nome}")
    fun lerPorNome(@PathVariable("nome") nome: String): ResponseEntity<List<UsuarioDTOResponse>> {
        val usuario = service.lerPorNome(nome)

        if (usuario.isEmpty()) {
            return ResponseEntity.status(404).build()
        }

        val resposta = usuario.map {
            mapper.toResponse(it)
        }

        return ResponseEntity.ok(resposta)
    }

    @PutMapping("/{id}")
    fun atualizar(
        @PathVariable("id") id: Long,
        @Valid @RequestBody usuarioDto: UsuarioDTORequest
    ): ResponseEntity<String> {
        return try {
            if (service.atualizar(id, mapper.toModel(usuarioDto))) {
                ResponseEntity.status(200).body("Usuário atualizado com sucesso")
            } else {
                ResponseEntity.status(404).body("Usuário com id: $id não foi encontrado")
            }
        } catch (_: Exception) {
            ResponseEntity.status(500).body("Erro ao atualizar o usuário")
        }
    }

    @PutMapping("/{id}/papeis")
    fun alterarPapel(
        @PathVariable("id") id: Long,
        @Valid @RequestBody papelDto: UsuarioPapelDTO
    ): ResponseEntity<String> {
        return try {
            if (service.alterarPapel(id, papelDto.papel)) {
                ResponseEntity.status(200).body("Papel do usuário alterado com sucesso")
            } else {
                ResponseEntity.status(404).body("Usuário com id: $id não foi encontrado")
            }
        } catch (_: Exception) {
            ResponseEntity.status(500).body("Erro ao alterar o papel do usuário")
        }
    }

    @DeleteMapping("/{id}")
    fun apagar(@PathVariable("id") id: Long): ResponseEntity<String> {
        return try {
            if (service.apagar(id)) {
                ResponseEntity.status(200).body("Usuário removido com sucesso")
            } else {
                ResponseEntity.status(404).body("Usuário com id: $id não foi encontrado")
            }
        } catch (_: Exception) {
            ResponseEntity.status(500).body("Erro ao remover o usuário")
        }
    }
}