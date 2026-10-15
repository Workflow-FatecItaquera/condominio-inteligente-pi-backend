package com.example.condominio.controller

import com.example.condominio.dto.OcorrenciaDTORequest
import com.example.condominio.dto.OcorrenciaDTOResponse
import com.example.condominio.mapper.OcorrenciaMapper
import com.example.condominio.service.OcorrenciaService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/ocorrencias")
class OcorrenciaController(
    private val service: OcorrenciaService,
    private val mapper: OcorrenciaMapper
) {

    @GetMapping
    fun lerTodos(): List<OcorrenciaDTOResponse> {
        return service.lerTodos().map { mapper.toResponse(it) }
    }

    @GetMapping("/{id}")
    fun lerPorId(@PathVariable("id") id: Long): ResponseEntity<OcorrenciaDTOResponse> {
        val ocorrencia = service.lerPorId(id)
            ?: return ResponseEntity.status(404).build()
        return ResponseEntity.ok(mapper.toResponse(ocorrencia))
    }

    @PostMapping
    fun cadastrar(@Valid @RequestBody ocorrenciaDto: OcorrenciaDTORequest): ResponseEntity<String> {
        return try {
            service.cadastrar(mapper.toModel(ocorrenciaDto))
            ResponseEntity.status(201).body("Ocorrência salva com sucesso")
        } catch (_: Exception) {
            ResponseEntity.status(500).body("Erro ao salvar a ocorrência")
        }
    }

    @PutMapping("/{id}")
    fun atualizar(
        @PathVariable("id") id: Long,
        @Valid @RequestBody ocorrenciaDto: OcorrenciaDTORequest
    ): ResponseEntity<String> {
        return try {
            if (service.atualizar(id, mapper.toModel(ocorrenciaDto))) {
                ResponseEntity.ok("Ocorrência atualizada com sucesso")
            } else {
                ResponseEntity.status(404).body("Ocorrência com id: $id não foi encontrada")
            }
        } catch (_: Exception) {
            ResponseEntity.status(500).body("Erro ao atualizar a ocorrência")
        }
    }

    @DeleteMapping("/{id}")
    fun apagar(@PathVariable("id") id: Long): ResponseEntity<String> {
        return try {
            if (service.apagar(id)) {
                ResponseEntity.ok("Ocorrência removida com sucesso")
            } else {
                ResponseEntity.status(404).body("Ocorrência com id: $id não foi encontrada")
            }
        } catch (_: Exception) {
            ResponseEntity.status(500).body("Erro ao remover a ocorrência")
        }
    }
}
