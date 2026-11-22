package com.example.condominio.controller

import com.example.condominio.dto.AutorizacaoDTORequest
import com.example.condominio.dto.AutorizacaoDTOResponse
import com.example.condominio.dto.ListaAutorizadosDTORequest
import com.example.condominio.dto.ListaAutorizadosDTOResponse
import com.example.condominio.mapper.ControleAcessoMapper
import com.example.condominio.security.UserDetailImplementation
import com.example.condominio.service.AutorizacaoService
import com.example.condominio.service.Relogio
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

/** Manutenção de autorizações e listas pelo proprietário/responsável. */
@RestController
@RequestMapping("/api")
class AutorizacaoController(
    private val service: AutorizacaoService,
    private val relogio: Relogio,
    private val mapper: ControleAcessoMapper
) {

    @PostMapping("/autorizacoes")
    fun conceder(
        @Valid @RequestBody dto: AutorizacaoDTORequest,
        authentication: Authentication
    ): ResponseEntity<Any> {
        return try {
            val autorizacao = service.conceder(
                dto.visitanteId,
                dto.unidadeId,
                idDoUsuario(authentication),
                dto.dataInicio!!,
                dto.dataFim!!,
                dto.horaInicio!!,
                dto.horaFim!!,
                dto.permiteFimSemana,
                dto.permiteFeriado
            ) ?: return ResponseEntity.status(404).body("Visitante ou unidade não encontrados")
            ResponseEntity.status(201).body(mapper.toResponse(autorizacao, relogio.agora()))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(400).body(e.message ?: "Período inválido")
        }
    }

    @GetMapping("/autorizacoes")
    fun lerTodas(): List<AutorizacaoDTOResponse> {
        val agora = relogio.agora()
        return service.lerAutorizacoes().map { mapper.toResponse(it, agora) }
    }

    @GetMapping("/autorizacoes/{id}")
    fun lerPorId(@PathVariable("id") id: Long): ResponseEntity<AutorizacaoDTOResponse> {
        val autorizacao = service.lerAutorizacao(id) ?: return ResponseEntity.status(404).build()
        return ResponseEntity.ok(mapper.toResponse(autorizacao, relogio.agora()))
    }

    /** RN05: cancelamento a qualquer momento. */
    @PutMapping("/autorizacoes/{id}/cancelar")
    fun cancelar(@PathVariable("id") id: Long): ResponseEntity<String> {
        return if (service.cancelar(id, relogio.agora())) {
            ResponseEntity.ok("Autorização cancelada com sucesso")
        } else {
            ResponseEntity.status(404).body("Autorização com id: $id não foi encontrada")
        }
    }

    /** RN01: atualização da lista de autorizados da unidade. */
    @PutMapping("/listas/{unidadeId}")
    fun revisarLista(
        @PathVariable("unidadeId") unidadeId: Long,
        @Valid @RequestBody dto: ListaAutorizadosDTORequest,
        authentication: Authentication
    ): ResponseEntity<Any> {
        val atualizadaEm = dto.atualizadaEm ?: relogio.agora().toLocalDate()
        val lista = service.revisarLista(unidadeId, dto.visitanteIds, idDoUsuario(authentication), atualizadaEm)
            ?: return ResponseEntity.status(404).body("Unidade com id: $unidadeId não foi encontrada")
        return ResponseEntity.ok(mapper.toResponse(lista))
    }

    @GetMapping("/listas/{unidadeId}")
    fun lerLista(@PathVariable("unidadeId") unidadeId: Long): ResponseEntity<ListaAutorizadosDTOResponse> {
        val lista = service.lerListaVigente(unidadeId) ?: return ResponseEntity.status(404).build()
        return ResponseEntity.ok(mapper.toResponse(lista))
    }

    private fun idDoUsuario(authentication: Authentication): Long? {
        val principal = authentication.principal
        if (principal is UserDetailImplementation) {
            return principal.usuario.id
        }
        return null
    }
}
