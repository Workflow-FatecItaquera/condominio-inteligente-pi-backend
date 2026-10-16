package com.example.condominio.controller

import com.example.condominio.dto.EncomendaDTORequest
import com.example.condominio.dto.EncomendaDTOResponse
import com.example.condominio.mapper.EncomendaMapper
import com.example.condominio.service.EncomendaService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/encomendas")
class EncomendaController(
    private val service: EncomendaService,
    private val mapper: EncomendaMapper
) {

    @GetMapping
    fun lerTodos(): List<EncomendaDTOResponse> {
        return service.lerTodos().map { mapper.toResponse(it) }
    }

    @GetMapping("/{id}")
    fun lerPorId(@PathVariable("id") id: Long): ResponseEntity<EncomendaDTOResponse> {
        val encomenda = service.lerPorId(id)
            ?: return ResponseEntity.status(404).build()
        return ResponseEntity.ok(mapper.toResponse(encomenda))
    }

    @GetMapping("/busca/{codigo}")
    fun buscaPorCodigo(
        @RequestParam("codigo") codigo: String
    ): List<EncomendaDTOResponse> {
        return service.buscaPorCodigo(codigo)
            .map { mapper.toResponse(it) }
    }

    @PostMapping
    fun cadastrar(@Valid @RequestBody encomendaDto: EncomendaDTORequest): ResponseEntity<String> {
        return try {
            service.cadastrar(mapper.toModel(encomendaDto))
            ResponseEntity.status(201).body("Encomenda salva com sucesso")
        } catch (_: Exception) {
            ResponseEntity.status(500).body("Erro ao salvar a encomenda")
        }
    }

    @PutMapping("/{id}")
    fun atualizar(
        @PathVariable("id") id: Long,
        @Valid @RequestBody encomendaDto: EncomendaDTORequest
    ): ResponseEntity<String> {
        return try {
            if (service.atualizar(id, mapper.toModel(encomendaDto))) {
                ResponseEntity.ok("Encomenda atualizada com sucesso")
            } else {
                ResponseEntity.status(404).body("Encomenda com id: $id não foi encontrada")
            }
        } catch (_: Exception) {
            ResponseEntity.status(500).body("Erro ao atualizar a encomenda")
        }
    }

    @DeleteMapping("/{id}")
    fun apagar(@PathVariable("id") id: Long): ResponseEntity<String> {
        return try {
            if (service.apagar(id)) {
                ResponseEntity.ok("Encomenda removida com sucesso")
            } else {
                ResponseEntity.status(404).body("Encomenda com id: $id não foi encontrada")
            }
        } catch (_: Exception) {
            ResponseEntity.status(500).body("Erro ao remover a encomenda")
        }
    }
}
