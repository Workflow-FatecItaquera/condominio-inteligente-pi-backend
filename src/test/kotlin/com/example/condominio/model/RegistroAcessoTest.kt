package com.example.condominio.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

/** RN04: a saída é gravada no mesmo registro da entrada. */
class RegistroAcessoTest {

    private val entrada = LocalDateTime.of(2026, 12, 1, 10, 0)

    @Test
    fun `registro novo esta com a visita aberta`() {
        val registro = RegistroAcesso(id = 5L, unidadeId = 101L, porteiroId = 9L, entradaEm = entrada)

        assertTrue(registro.visitaAberta())
    }

    @Test
    fun `registrarSaida preenche o mesmo registro e encerra a visita`() {
        val registro = RegistroAcesso(id = 5L, unidadeId = 101L, porteiroId = 9L, entradaEm = entrada)
        val saida = LocalDateTime.of(2026, 12, 1, 12, 30)

        val encerrado = registro.registrarSaida(saida, 10L)

        assertEquals(5L, encerrado.id)
        assertEquals(entrada, encerrado.entradaEm)
        assertEquals(saida, encerrado.saidaEm)
        assertEquals(10L, encerrado.porteiroSaidaId)
        assertFalse(encerrado.visitaAberta())
    }
}
