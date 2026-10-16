package com.example.condominio.controller

import com.example.condominio.dto.ReservaDTORequest
import com.example.condominio.dto.ReservaDTOResponse
import com.example.condominio.mapper.ReservaMapper
import com.example.condominio.service.ReservaService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/reservas")
class ReservaController(
    private val service: ReservaService,
    private val mapper: ReservaMapper
) {

    @GetMapping
    fun lerTodos(): List<ReservaDTOResponse> {
        return service.lerTodos().map { mapper.toResponse(it) }
    }

    @GetMapping("/{id}")
    fun lerPorId(@PathVariable("id") id: Long): ResponseEntity<ReservaDTOResponse> {
        val reserva = service.lerPorId(id)
            ?: return ResponseEntity.status(404).build()
        return ResponseEntity.ok(mapper.toResponse(reserva))
    }

    @PostMapping
    fun cadastrar(@Valid @RequestBody reservaDto: ReservaDTORequest): ResponseEntity<String> {
        return try {
            service.cadastrar(mapper.toModel(reservaDto))
            ResponseEntity.status(201).body("Reserva salva com sucesso")
        } catch (_: Exception) {
            ResponseEntity.status(500).body("Erro ao salvar a reserva")
        }
    }

    @PutMapping("/{id}")
    fun atualizar(
        @PathVariable("id") id: Long,
        @Valid @RequestBody reservaDto: ReservaDTORequest
    ): ResponseEntity<String> {
        return try {
            if (service.atualizar(id, mapper.toModel(reservaDto))) {
                ResponseEntity.status(200).body("Reserva atualizada com sucesso")
            } else {
                ResponseEntity.status(404).body("Reserva com id: $id não foi encontrada")
            }
        } catch (_: Exception) {
            ResponseEntity.status(500).body("Erro ao atualizar a reserva")
        }
    }

    @DeleteMapping("/{id}")
    fun apagar(@PathVariable("id") id: Long): ResponseEntity<String> {
        return try {
            if (service.apagar(id)) {
                ResponseEntity.status(200).body("Reserva removida com sucesso")
            } else {
                ResponseEntity.status(404).body("Reserva com id: $id não foi encontrada")
            }
        } catch (_: Exception) {
            ResponseEntity.status(500).body("Erro ao remover a reserva")
        }
    }


    @GetMapping("/busca")
    fun buscarPorMotivo(
        @RequestParam motivo: String
    ): List<ReservaDTOResponse> {
        return service.buscarPorMotivo(motivo)
            .map { mapper.toResponse(it) }
    }
}