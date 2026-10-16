package com.example.condominio.controller

import com.example.condominio.dto.PagamentoRegistradoDTORequest
import com.example.condominio.dto.PagamentoRegistradoDTOResponse
import com.example.condominio.mapper.PagamentoRegistradoMapper
import com.example.condominio.service.PagamentoRegistradoService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/pagamentos")
class PagamentoRegistradoController(
    private val service: PagamentoRegistradoService,
    private val mapper: PagamentoRegistradoMapper
) {

    @GetMapping
    fun lerTodos(): List<PagamentoRegistradoDTOResponse> {
        return service.lerTodos().map { mapper.toResponse(it) }
    }

    @GetMapping("/{id}")
    fun lerPorId(@PathVariable("id") id: Long): ResponseEntity<PagamentoRegistradoDTOResponse> {
        val pagamento = service.lerPorId(id)
            ?: return ResponseEntity.status(404).build()
        return ResponseEntity.ok(mapper.toResponse(pagamento))
    }

    @PostMapping
    fun cadastrar(@Valid @RequestBody pagamentoDto: PagamentoRegistradoDTORequest): ResponseEntity<String> {
        return try {
            service.cadastrar(mapper.toModel(pagamentoDto))
            ResponseEntity.status(201).body("Pagamento salvo com sucesso")
        } catch (_: Exception) {
            ResponseEntity.status(500).body("Erro ao salvar o pagamento")
        }
    }

    @PutMapping("/{id}")
    fun atualizar(
        @PathVariable("id") id: Long,
        @Valid @RequestBody pagamentoDto: PagamentoRegistradoDTORequest
    ): ResponseEntity<String> {
        return try {
            if (service.atualizar(id, mapper.toModel(pagamentoDto))) {
                ResponseEntity.status(200).body("Pagamento atualizado com sucesso")
            } else {
                ResponseEntity.status(404).body("Pagamento com id: $id não foi encontrado")
            }
        } catch (_: Exception) {
            ResponseEntity.status(500).body("Erro ao atualizar o pagamento")
        }
    }

    @DeleteMapping("/{id}")
    fun apagar(@PathVariable("id") id: Long): ResponseEntity<String> {
        return try {
            if (service.apagar(id)) {
                ResponseEntity.status(200).body("Pagamento removido com sucesso")
            } else {
                ResponseEntity.status(404).body("Pagamento com id: $id não foi encontrado")
            }
        } catch (_: Exception) {
            ResponseEntity.status(500).body("Erro ao remover o pagamento")
        }
    }

    @GetMapping("/busca/{formaPagamento}")
    fun buscarPorFormaPagamento(
        @PathVariable("formaPagamento") formaPagamento: String
    ): ResponseEntity<List<PagamentoRegistradoDTOResponse>> {

        val resultado = service.buscarPorFormaPagamento(formaPagamento)

        if (resultado.isEmpty()) {
            return ResponseEntity.notFound().build()
        }

        val resposta = resultado.map {
            mapper.toResponse(it)
        }

        return ResponseEntity.ok(resposta)
    }
}