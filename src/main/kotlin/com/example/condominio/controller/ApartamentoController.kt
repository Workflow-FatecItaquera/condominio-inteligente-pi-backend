package com.example.condominio.controller

import com.example.condominio.dto.ApartamentoDTORequest
import com.example.condominio.dto.ApartamentoDTOResponse
import com.example.condominio.dto.CondominioDTOResponse
import com.example.condominio.mapper.ApartamentoMapper
import com.example.condominio.service.ApartamentoService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/apartamentos")
class ApartamentoController(
    private val service: ApartamentoService,
    private val mapper: ApartamentoMapper
) {

    @GetMapping
    fun lerTodos(): List<ApartamentoDTOResponse> {
        return service.lerTodos().map { mapper.toResponse(it) }
    }

    @GetMapping("/{id}")
    fun lerPorId(@PathVariable("id") id: Long): ResponseEntity<ApartamentoDTOResponse> {
        val apartamento = service.lerPorId(id)
            ?: return ResponseEntity.status(404).build()
        return ResponseEntity.ok(mapper.toResponse(apartamento))
    }

    @GetMapping("/busca/{bloco}")
    fun lerPorBloco(@PathVariable("bloco") bloco: String): ResponseEntity<List<ApartamentoDTOResponse>> {
        val apartamento = service.lerPorBloco(bloco)
        if (apartamento.isEmpty()) {
            return ResponseEntity.status(404).build()
        }
        val resposta = apartamento.map { mapper.toResponse(it) }
        return ResponseEntity.ok(resposta)
    }

    @PostMapping
    fun cadastrar(@Valid @RequestBody apartamentoDto: ApartamentoDTORequest): ResponseEntity<String> {
        return try {
            service.cadastrar(mapper.toModel(apartamentoDto))
            ResponseEntity.status(201).body("Apartamento salvo com sucesso")
        } catch (_: Exception) {
            ResponseEntity.status(500).body("Erro ao salvar o apartamento")
        }
    }

    @PutMapping("/{id}")
    fun atualizar(
        @PathVariable("id") id: Long,
        @Valid @RequestBody apartamentoDto: ApartamentoDTORequest
    ): ResponseEntity<String> {
        return try {
            if (service.atualizar(id, mapper.toModel(apartamentoDto))) {
                ResponseEntity.status(200).body("Apartamento atualizado com sucesso")
            } else {
                ResponseEntity.status(404).body("Apartamento com id: $id não foi encontrado")
            }
        } catch (_: Exception) {
            ResponseEntity.status(500).body("Erro ao atualizar o apartamento")
        }
    }

    @DeleteMapping("/{id}")
    fun apagar(@PathVariable("id") id: Long): ResponseEntity<String> {
        return try {
            if (service.apagar(id)) {
                ResponseEntity.status(200).body("Apartamento removido com sucesso")
            } else {
                ResponseEntity.status(404).body("Apartamento com id: $id não foi encontrado")
            }
        } catch (_: Exception) {
            ResponseEntity.status(500).body("Erro ao remover o apartamento")
        }
    }
}