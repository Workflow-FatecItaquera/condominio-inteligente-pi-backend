package com.example.condominio.controller

import com.example.condominio.dto.VisitanteDTORequest
import com.example.condominio.dto.VisitanteDTOResponse
import com.example.condominio.mapper.VisitanteMapper
import com.example.condominio.service.VisitanteService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/visitantes")
class VisitanteController(
    private val service: VisitanteService,
    private val mapper: VisitanteMapper
) {

    @GetMapping
    fun lerTodos(): List<VisitanteDTOResponse> {
        return service.lerTodos().map { mapper.toResponse(it) }
    }

    @GetMapping("/{id}")
    fun lerPorId(@PathVariable("id") id: Long): ResponseEntity<VisitanteDTOResponse> {
        val visitante = service.lerPorId(id)
            ?: return ResponseEntity.status(404).build()
        return ResponseEntity.ok(mapper.toResponse(visitante))
    }

    @PostMapping
    fun cadastrar(@Valid @RequestBody visitanteDto: VisitanteDTORequest): ResponseEntity<String> {
        return try {
            service.cadastrar(mapper.toModel(visitanteDto))
            ResponseEntity.status(201).body("Visitante salvo com sucesso")
        } catch (_: Exception) {
            ResponseEntity.status(500).body("Erro ao salvar o visitante")
        }
    }

    @PutMapping("/{id}")
    fun atualizar(
        @PathVariable("id") id: Long,
        @Valid @RequestBody visitanteDto: VisitanteDTORequest
    ): ResponseEntity<String> {
        return try {
            if (service.atualizar(id, mapper.toModel(visitanteDto))) {
                ResponseEntity.status(200).body("Visitante atualizado com sucesso")
            } else {
                ResponseEntity.status(404).body("Visitante com id: $id não foi encontrado")
            }
        } catch (_: Exception) {
            ResponseEntity.status(500).body("Erro ao atualizar o visitante")
        }
    }

    @DeleteMapping("/{id}")
    fun apagar(@PathVariable("id") id: Long): ResponseEntity<String> {
        return try {
            if (service.apagar(id)) {
                ResponseEntity.status(200).body("Visitante removido com sucesso")
            } else {
                ResponseEntity.status(404).body("Visitante com id: $id não foi encontrado")
            }
        } catch (_: Exception) {
            ResponseEntity.status(500).body("Erro ao remover o visitante")
        }
    }

    @GetMapping("/busca")
    fun buscarPorNome(
        @RequestParam nome: String
    ): List<VisitanteDTOResponse> {
        return service.buscarPorNome(nome)
            .map { mapper.toResponse(it) }
    }
}