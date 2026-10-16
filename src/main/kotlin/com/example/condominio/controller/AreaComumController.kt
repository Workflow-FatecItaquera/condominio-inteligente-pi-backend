package com.example.condominio.controller

import com.example.condominio.dto.AreaComumDTORequest
import com.example.condominio.dto.AreaComumDTOResponse
import com.example.condominio.mapper.AreaComumMapper
import com.example.condominio.service.AreaComumService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/areascomuns")
class AreaComumController(
    private val service: AreaComumService,
    private val mapper: AreaComumMapper
) {

    @GetMapping
    fun lerTodos(): List<AreaComumDTOResponse> {
        return service.lerTodos().map { mapper.toResponse(it) }
    }

    @GetMapping("/{id}")
    fun lerPorId(@PathVariable("id") id: Long): ResponseEntity<AreaComumDTOResponse> {
        val areaComum = service.lerPorId(id)
            ?: return ResponseEntity.status(404).build()
        return ResponseEntity.ok(mapper.toResponse(areaComum))
    }

    @PostMapping
    fun cadastrar(@Valid @RequestBody areaComumDto: AreaComumDTORequest): ResponseEntity<String> {
        return try {
            service.cadastrar(mapper.toModel(areaComumDto))
            ResponseEntity.status(201).body("Área comum salva com sucesso")
        } catch (_: Exception) {
            ResponseEntity.status(500).body("Erro ao salvar a área comum")
        }
    }

    @PutMapping("/{id}")
    fun atualizar(
        @PathVariable("id") id: Long,
        @Valid @RequestBody areaComumDto: AreaComumDTORequest
    ): ResponseEntity<String> {
        return try {
            if (service.atualizar(id, mapper.toModel(areaComumDto))) {
                ResponseEntity.status(200).body("Área comum atualizada com sucesso")
            } else {
                ResponseEntity.status(404).body("Área comum com id: $id não foi encontrada")
            }
        } catch (_: Exception) {
            ResponseEntity.status(500).body("Erro ao atualizar a área comum")
        }
    }

    @DeleteMapping("/{id}")
    fun apagar(@PathVariable("id") id: Long): ResponseEntity<String> {
        return try {
            if (service.apagar(id)) {
                ResponseEntity.status(200).body("Área comum removida com sucesso")
            } else {
                ResponseEntity.status(404).body("Área comum com id: $id não foi encontrada")
            }
        } catch (_: Exception) {
            ResponseEntity.status(500).body("Erro ao remover a área comum")
        }
    }

    @GetMapping("/busca/{nome}")
    fun buscarPorNome(
        @PathVariable("nome") nome: String
    ): ResponseEntity<List<AreaComumDTOResponse>> {

        val resultado = service.buscarPorNome(nome)

        if (resultado.isEmpty()) {
            return ResponseEntity.notFound().build()
        }

        val resposta = resultado.map {
            mapper.toResponse(it)
        }

        return ResponseEntity.ok(resposta)
    }
}