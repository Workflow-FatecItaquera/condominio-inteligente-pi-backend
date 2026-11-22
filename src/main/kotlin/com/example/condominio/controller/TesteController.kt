package com.example.condominio.controller

import com.example.condominio.service.FalhasControlaveis
import com.example.condominio.service.MassaTesteService
import com.example.condominio.service.PontoDeFalha
import com.example.condominio.service.RelogioControlavel
import org.springframework.context.annotation.Profile
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import java.time.format.DateTimeParseException

/**
 * Apoio aos testes de sistema. Existe somente com o perfil "teste" e fica sob /admin
 * (exige papel ADMIN), então nunca é publicado junto com o ambiente de produção.
 */
@RestController
@RequestMapping("/admin/teste")
@Profile("teste")
class TesteController(
    private val relogio: RelogioControlavel,
    private val massa: MassaTesteService,
    private val falhas: FalhasControlaveis
) {

    @GetMapping("/relogio")
    fun lerRelogio(): Map<String, String> {
        return mapOf("agora" to relogio.agora().toString())
    }

    /** Corpo: {"dataHora": "2026-12-01T10:00:00"}. O tempo continua correndo a partir do valor definido. */
    @PutMapping("/relogio")
    fun definirRelogio(@RequestBody corpo: Map<String, String>): ResponseEntity<Any> {
        val texto = corpo["dataHora"] ?: return ResponseEntity.status(400).body("Informe dataHora")
        return try {
            relogio.definir(LocalDateTime.parse(texto))
            ResponseEntity.ok(mapOf("agora" to relogio.agora().toString()))
        } catch (e: DateTimeParseException) {
            ResponseEntity.status(400).body("dataHora inválida. Use o formato 2026-12-01T10:00:00")
        }
    }

    @DeleteMapping("/relogio")
    fun restaurarRelogio(): Map<String, String> {
        relogio.restaurar()
        return mapOf("agora" to relogio.agora().toString())
    }

    /** Restaura a massa-base B0 relativa ao dia atual do relógio de teste. */
    @PostMapping("/massa-b0")
    fun restaurarMassa(): Map<String, Any> {
        return massa.restaurarB0()
    }

    /** Liga uma falha induzida: CONSULTA, GRAVAR_ENTRADA ou GRAVAR_SAIDA (CT30, CT31 e CT34). */
    @PutMapping("/falhas/{ponto}")
    fun ativarFalha(@PathVariable("ponto") ponto: String): ResponseEntity<Any> {
        val escolhido = PontoDeFalha.values().firstOrNull { it.name == ponto.uppercase() }
            ?: return ResponseEntity.status(400).body("Ponto inválido. Use CONSULTA, GRAVAR_ENTRADA ou GRAVAR_SAIDA")
        falhas.ativar(escolhido)
        return ResponseEntity.ok(mapOf("ativas" to falhas.ativas().map { it.name }))
    }

    @DeleteMapping("/falhas")
    fun limparFalhas(): Map<String, List<String>> {
        falhas.limpar()
        return mapOf("ativas" to emptyList())
    }

    @GetMapping("/falhas")
    fun lerFalhas(): Map<String, List<String>> {
        return mapOf("ativas" to falhas.ativas().map { it.name })
    }
}
