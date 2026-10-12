package com.example.condominio.controller

import com.example.condominio.dto.CondominioDTORequest
import com.example.condominio.dto.CondominioDTOResponse
import com.example.condominio.mapper.CondominioMapper
import com.example.condominio.service.CondominioService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/condominios")
class CondominioController(
    private val service: CondominioService,
    private val mapper: CondominioMapper
) {

    @GetMapping
    fun lerTodos(): List<CondominioDTOResponse> {
        return service.lerTodos().map { mapper.toResponse(it) }
    }

    @GetMapping("/{id}")
    fun lerPorId(@PathVariable("id") id: Long): ResponseEntity<CondominioDTOResponse> {
        val condominio = service.lerPorId(id)
            ?: return ResponseEntity.status(404).build()
        return ResponseEntity.ok(mapper.toResponse(condominio))
    }

    @GetMapping("/busca/{nome}")
    fun lerPorNome(@PathVariable("nome") nome: String): ResponseEntity<List<CondominioDTOResponse>> {
        val condominios = service.lerPorNome(nome)
        if (condominios.isEmpty()) {
            return ResponseEntity.status(404).build()
        }
        val resposta = condominios.map { mapper.toResponse(it) }
        return ResponseEntity.ok(resposta)
    }

    @PostMapping
    fun cadastrar(@Valid @RequestBody condominioDto: CondominioDTORequest): ResponseEntity<String> {
        return try {
            service.cadastrar(mapper.toModel(condominioDto))
            ResponseEntity.status(201).body("Condomínio salvo com sucesso")
        } catch (_: Exception) {
            ResponseEntity.status(500).body("Erro ao salvar o condomínio")
        }
    }

    @PutMapping("/{id}")
    fun atualizar(
        @PathVariable("id") id: Long,
        @Valid @RequestBody condominioDto: CondominioDTORequest
    ): ResponseEntity<String> {
        return try {
            if (service.atualizar(id, mapper.toModel(condominioDto))) {
                ResponseEntity.status(200).body("Condomínio atualizado com sucesso")
            } else {
                ResponseEntity.status(404).body("Condomínio com id: $id não foi encontrado")
            }
        } catch (_: Exception) {
            ResponseEntity.status(500).body("Erro ao atualizar o condomínio")
        }
    }

    @DeleteMapping("/{id}")
    fun apagar(@PathVariable("id") id: Long): ResponseEntity<String> {
        return try {
            if (service.apagar(id)) {
                ResponseEntity.status(200).body("Condomínio removido com sucesso")
            } else {
                ResponseEntity.status(404).body("Condomínio com id: $id não foi encontrado")
            }
        } catch (_: Exception) {
            ResponseEntity.status(500).body("Erro ao remover o condomínio")
        }
    }
}