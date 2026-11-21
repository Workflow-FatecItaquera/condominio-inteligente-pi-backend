package com.example.condominio.service

import com.example.condominio.model.Visitante
import com.example.condominio.repository.VisitanteRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify

class VisitanteServiceTest {

    private lateinit var repository: VisitanteRepository
    private lateinit var service: VisitanteService

    @BeforeEach
    fun preparar() {
        // Mock usado para testar o servico sem acessar o banco de dados.
        repository = mock(VisitanteRepository::class.java)
        service = VisitanteService(repository)
    }

    @Test
    fun `deve cadastrar visitante`() {
        val visitante = visitante()

        service.cadastrar(visitante)

        verify(repository).save(visitante)
    }

    @Test
    fun `deve listar visitantes ativos`() {
        val visitantes = listOf(visitante(id = 1), visitante(id = 2, nome = "Ana"))
        `when`(repository.findAllByActiveTrue()).thenReturn(visitantes)

        val resultado = service.lerTodos()

        assertEquals(visitantes, resultado)
    }

    @Test
    fun `deve retornar visitante existente por id`() {
        val visitante = visitante(id = 1)
        `when`(repository.findByIdAndActiveTrue(1)).thenReturn(visitante)

        val resultado = service.lerPorId(1)

        assertSame(visitante, resultado)
    }

    @Test
    fun `deve retornar nulo para id inexistente`() {
        `when`(repository.findByIdAndActiveTrue(99)).thenReturn(null)

        val resultado = service.lerPorId(99)

        assertNull(resultado)
    }

    @Test
    fun `deve inativar visitante existente`() {
        val visitante = visitante(id = 1, active = true)
        `when`(repository.findByIdAndActiveTrue(1)).thenReturn(visitante)

        val resultado = service.apagar(1)

        assertTrue(resultado)
        verify(repository).save(visitante.copy(active = false))
    }

    @Test
    fun `nao deve apagar visitante inexistente`() {
        `when`(repository.findByIdAndActiveTrue(99)).thenReturn(null)

        val resultado = service.apagar(99)

        assertFalse(resultado)
        verify(repository, never()).save(any(Visitante::class.java))
    }

    @Test
    fun `deve atualizar visitante existente preservando id e situacao`() {
        val existente = visitante(id = 1, active = true)
        val novosDados = visitante(nome = "Nome atualizado", active = false)
        `when`(repository.findByIdAndActiveTrue(1)).thenReturn(existente)

        val resultado = service.atualizar(1, novosDados)

        assertTrue(resultado)
        verify(repository).save(novosDados.copy(id = 1, active = true))
    }

    @Test
    fun `nao deve atualizar visitante inexistente`() {
        `when`(repository.findByIdAndActiveTrue(99)).thenReturn(null)

        val resultado = service.atualizar(99, visitante())

        assertFalse(resultado)
        verify(repository, never()).save(any(Visitante::class.java))
    }

    @Test
    fun `deve buscar visitante por nome`() {
        val visitantes = listOf(visitante(id = 1, nome = "Maria"))
        `when`(repository.buscarPorNome("Maria")).thenReturn(visitantes)

        val resultado = service.buscarPorNome("Maria")

        assertEquals(visitantes, resultado)
    }

    private fun visitante(
        id: Long? = null,
        nome: String = "Visitante Teste",
        active: Boolean = true
    ) = Visitante(
        id = id,
        nome = nome,
        visitaAutorizada = true,
        documento = "123456789",
        active = active
    )
}
