package com.example.condominio.controller

import com.example.condominio.dto.CobrancaCondominialDTORequest
import com.example.condominio.dto.CobrancaCondominialDTOResponse
import com.example.condominio.mapper.CobrancaCondominialMapper
import com.example.condominio.service.CobrancaCondominialService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/cobrancas")
class CobrancaCondominialController(
    private val service: CobrancaCondominialService,
    private val mapper: CobrancaCondominialMapper
) {

    @GetMapping
    fun lerTodos(): List<CobrancaCondominialDTOResponse> {
        return service.lerTodos().map { mapper.toResponse(it) }
    }

    @GetMapping("/{id}")
    fun lerPorId(@PathVariable("id") id: Long): ResponseEntity<CobrancaCondominialDTOResponse> {
        val cobranca = service.lerPorId(id)
            ?: return ResponseEntity.status(404).build()
        return ResponseEntity.ok(mapper.toResponse(cobranca))
    }

    @PostMapping
    fun cadastrar(@Valid @RequestBody cobrancaDto: CobrancaCondominialDTORequest): ResponseEntity<String> {
        return try {
            service.cadastrar(mapper.toModel(cobrancaDto))
            ResponseEntity.status(201).body("Cobrança salva com sucesso")
        } catch (_: Exception) {
            ResponseEntity.status(500).body("Erro ao salvar a cobrança")
        }
    }

    @PutMapping("/{id}")
    fun atualizar(
        @PathVariable("id") id: Long,
        @Valid @RequestBody cobrancaDto: CobrancaCondominialDTORequest
    ): ResponseEntity<String> {
        return try {
            if (service.atualizar(id, mapper.toModel(cobrancaDto))) {
                ResponseEntity.status(200).body("Cobrança atualizada com sucesso")
            } else {
                ResponseEntity.status(404).body("Cobrança com id: $id não foi encontrada")
            }
        } catch (_: Exception) {
            ResponseEntity.status(500).body("Erro ao atualizar a cobrança")
        }
    }

    @DeleteMapping("/{id}")
    fun apagar(@PathVariable("id") id: Long): ResponseEntity<String> {
        return try {
            if (service.apagar(id)) {
                ResponseEntity.status(200).body("Cobrança removida com sucesso")
            } else {
                ResponseEntity.status(404).body("Cobrança com id: $id não foi encontrada")
            }
        } catch (_: Exception) {
            ResponseEntity.status(500).body("Erro ao remover a cobrança")
        }
    }

    @GetMapping("/busca")
    fun buscarPorDescricao(
        @RequestParam descricao: String
    ): List<CobrancaCondominialDTOResponse> {
        return service.buscarPorDescricao(descricao)
            .map { mapper.toResponse(it) }
    }
}