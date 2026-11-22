package com.example.condominio.controller

import com.example.condominio.dto.FeriadoDTORequest
import com.example.condominio.dto.FeriadoDTOResponse
import com.example.condominio.mapper.ControleAcessoMapper
import com.example.condominio.model.Feriado
import com.example.condominio.repository.FeriadoRepository
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/** Calendário de feriados usado pela RN02. */
@RestController
@RequestMapping("/api/feriados")
class FeriadoController(
    private val repository: FeriadoRepository,
    private val mapper: ControleAcessoMapper
) {

    @GetMapping
    fun lerTodos(): List<FeriadoDTOResponse> {
        return repository.findAll().map { mapper.toResponse(it) }
    }

    @PostMapping
    fun cadastrar(@Valid @RequestBody dto: FeriadoDTORequest): ResponseEntity<Any> {
        val data = dto.data!!
        if (repository.existsByData(data)) {
            return ResponseEntity.status(409).body("Já existe um feriado nesta data")
        }
        val salvo = repository.save(Feriado(data = data, nome = dto.nome))
        return ResponseEntity.status(201).body(mapper.toResponse(salvo))
    }

    @DeleteMapping("/{id}")
    fun apagar(@PathVariable("id") id: Long): ResponseEntity<String> {
        if (!repository.existsById(id)) {
            return ResponseEntity.status(404).body("Feriado com id: $id não foi encontrado")
        }
        repository.deleteById(id)
        return ResponseEntity.ok("Feriado removido com sucesso")
    }
}
