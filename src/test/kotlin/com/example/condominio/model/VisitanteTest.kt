package com.example.condominio.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class VisitanteTest {

    @Test
    fun `deve criar visitante com os dados informados`() {
        val visitante = Visitante(
            nome = "Visitante Teste",
            visitaAutorizada = true,
            documento = "123456789",
            active = true
        )

        assertEquals("Visitante Teste", visitante.nome)
        assertEquals("123456789", visitante.documento)
        assertTrue(visitante.visitaAutorizada)
        assertTrue(visitante.active)
    }

    @Test
    fun `deve criar copia inativa preservando os dados do visitante`() {
        val visitante = Visitante(
            id = 1,
            nome = "Visitante Teste",
            visitaAutorizada = true,
            documento = "123456789",
            active = true
        )

        val visitanteInativo = visitante.copy(active = false)

        assertFalse(visitanteInativo.active)
        assertEquals(visitante.id, visitanteInativo.id)
        assertEquals(visitante.nome, visitanteInativo.nome)
        assertEquals(visitante.documento, visitanteInativo.documento)
        assertEquals(visitante.visitaAutorizada, visitanteInativo.visitaAutorizada)
    }
}
