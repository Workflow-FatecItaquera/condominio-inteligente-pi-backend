package com.example.condominio.controller

import com.example.condominio.dto.AvaliarEntradaDTORequest
import com.example.condominio.dto.DecisaoAcessoDTOResponse
import com.example.condominio.dto.RegistrarEntradaDTORequest
import com.example.condominio.dto.RegistroAcessoDTOResponse
import com.example.condominio.mapper.ControleAcessoMapper
import com.example.condominio.security.UserDetailImplementation
import com.example.condominio.service.ControleAcessoService
import com.example.condominio.service.MotivoNegacao
import com.example.condominio.service.Relogio
import com.example.condominio.service.StatusSaida
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

/** Rotas do caso de uso Controle de Acesso de Visitantes (portaria). */
@RestController
@RequestMapping("/api/acessos")
class AcessoController(
    private val service: ControleAcessoService,
    private val relogio: Relogio,
    private val mapper: ControleAcessoMapper
) {

    /** Passos 1 a 4: consulta e avaliação das regras. Sempre 200, exceto indisponibilidade (503). */
    @PostMapping("/avaliar")
    fun avaliar(@Valid @RequestBody dto: AvaliarEntradaDTORequest): ResponseEntity<DecisaoAcessoDTOResponse> {
        val decisao = service.avaliarEntrada(dto.visitanteId, dto.unidadeId, dto.documento, relogio.agora())
        val status = if (decisao.motivo == MotivoNegacao.SERVICO_INDISPONIVEL) 503 else 200
        return ResponseEntity.status(status).body(mapper.toResponse(decisao))
    }

    /** Passos 5 e 6: confirma a liberação (revalida) e grava a entrada. 201, 403, 500 ou 503. */
    @PostMapping("/entrada")
    fun registrarEntrada(
        @Valid @RequestBody dto: RegistrarEntradaDTORequest,
        authentication: Authentication
    ): ResponseEntity<Any> {
        val resultado = service.registrarEntrada(dto.autorizacaoId, idDoUsuario(authentication), dto.documento, relogio.agora())
        val registro = resultado.registro
        if (registro != null) {
            return ResponseEntity.status(201).body(mapper.toResponse(registro))
        }
        val status = when (resultado.decisao.motivo) {
            MotivoNegacao.SERVICO_INDISPONIVEL -> 503
            MotivoNegacao.FALHA_AO_GRAVAR -> 500
            else -> 403
        }
        return ResponseEntity.status(status).body(mapper.toResponse(resultado.decisao))
    }

    /** Passos 7 e 8: registra a saída no mesmo registro da entrada. */
    @PutMapping("/{id}/saida")
    fun registrarSaida(
        @PathVariable("id") id: Long,
        authentication: Authentication
    ): ResponseEntity<Any> {
        val resultado = service.registrarSaida(id, idDoUsuario(authentication), relogio.agora())
        return when (resultado.status) {
            StatusSaida.REGISTRADA ->
                ResponseEntity.ok(mapper.toResponse(resultado.registro!!))
            StatusSaida.NAO_ENCONTRADO ->
                ResponseEntity.status(404).body("Registro de acesso com id: $id não foi encontrado")
            StatusSaida.JA_ENCERRADA ->
                ResponseEntity.status(409).body("A saída deste registro já foi registrada")
            StatusSaida.FALHA_AO_GRAVAR ->
                ResponseEntity.status(500).body("Falha ao gravar a saída. A visita continua em aberto")
        }
    }

    @GetMapping
    fun lerTodos(): List<RegistroAcessoDTOResponse> {
        return service.lerRegistros().map { mapper.toResponse(it) }
    }

    @GetMapping("/abertos")
    fun lerAbertos(): List<RegistroAcessoDTOResponse> {
        return service.lerAbertos().map { mapper.toResponse(it) }
    }

    @GetMapping("/{id}")
    fun lerPorId(@PathVariable("id") id: Long): ResponseEntity<RegistroAcessoDTOResponse> {
        val registro = service.lerRegistro(id) ?: return ResponseEntity.status(404).build()
        return ResponseEntity.ok(mapper.toResponse(registro))
    }

    private fun idDoUsuario(authentication: Authentication): Long {
        val principal = authentication.principal
        if (principal is UserDetailImplementation) {
            return principal.usuario.id ?: 0L
        }
        return 0L
    }
}
