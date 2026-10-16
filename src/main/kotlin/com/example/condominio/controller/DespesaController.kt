package com.example.condominio.controller

import com.example.condominio.dto.DespesaDTORequest
import com.example.condominio.dto.DespesaDTOResponse
import com.example.condominio.mapper.DespesaMapper
import com.example.condominio.service.DespesaService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/despesas")
class DespesaController(
    private val service: DespesaService,
    private val mapper: DespesaMapper
) {

    @GetMapping
    fun lerTodos(): List<DespesaDTOResponse> {
        return service.lerTodos().map { mapper.toResponse(it) }
    }

    @GetMapping("/{id}")
    fun lerPorId(@PathVariable("id") id: Long): ResponseEntity<DespesaDTOResponse> {
        val despesa = service.lerPorId(id)
            ?: return ResponseEntity.status(404).build()
        return ResponseEntity.ok(mapper.toResponse(despesa))
    }

    @PostMapping
    fun cadastrar(@Valid @RequestBody despesaDto: DespesaDTORequest): ResponseEntity<String> {
        return try {
            service.cadastrar(mapper.toModel(despesaDto))
            ResponseEntity.status(201).body("Despesa salva com sucesso")
        } catch (_: Exception) {
            ResponseEntity.status(500).body("Erro ao salvar a despesa")
        }
    }

    @PutMapping("/{id}")
    fun atualizar(
        @PathVariable("id") id: Long,
        @Valid @RequestBody despesaDto: DespesaDTORequest
    ): ResponseEntity<String> {
        return try {
            if (service.atualizar(id, mapper.toModel(despesaDto))) {
                ResponseEntity.status(200).body("Despesa atualizada com sucesso")
            } else {
                ResponseEntity.status(404).body("Despesa com id: $id não foi encontrada")
            }
        } catch (_: Exception) {
            ResponseEntity.status(500).body("Erro ao atualizar a despesa")
        }
    }

    @DeleteMapping("/{id}")
    fun apagar(@PathVariable("id") id: Long): ResponseEntity<String> {
        return try {
            if (service.apagar(id)) {
                ResponseEntity.status(200).body("Despesa removida com sucesso")
            } else {
                ResponseEntity.status(404).body("Despesa com id: $id não foi encontrada")
            }
        } catch (_: Exception) {
            ResponseEntity.status(500).body("Erro ao remover a despesa")
        }
    }

    @GetMapping("/busca")
    fun buscarPorCategoria(
        @RequestParam categoria: String
    ): List<DespesaDTOResponse> {
        return service.buscarPorCategoria(categoria)
            .map { mapper.toResponse(it) }
    }
}