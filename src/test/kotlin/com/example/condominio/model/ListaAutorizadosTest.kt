package com.example.condominio.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.LocalDate

/** RN01: validade de 6 meses de calendário, último dia inclusivo. */
class ListaAutorizadosTest {

    private val v01 = Visitante(id = 1L, nome = "V01", documento = "DOC-V01")
    private val v02 = Visitante(id = 2L, nome = "V02", documento = "DOC-V02")

    @Test
    fun `validaAte soma seis meses de calendario`() {
        val lista = ListaAutorizados(atualizadaEm = LocalDate.of(2026, 5, 11))

        assertEquals(LocalDate.of(2026, 11, 11), lista.validaAte())
    }

    @Test
    fun `lista vale no ultimo dia e deixa de valer no dia seguinte`() {
        val lista = ListaAutorizados(atualizadaEm = LocalDate.of(2026, 6, 1))

        assertTrue(lista.vigenteEm(LocalDate.of(2026, 11, 30)))
        assertTrue(lista.vigenteEm(LocalDate.of(2026, 12, 1)))
        assertFalse(lista.vigenteEm(LocalDate.of(2026, 12, 2)))
    }

    @Test
    fun `contem identifica apenas os visitantes da lista`() {
        val lista = ListaAutorizados(visitantes = mutableSetOf(v01))

        assertTrue(lista.contem(1L))
        assertFalse(lista.contem(2L))
        assertFalse(lista.contem(null))
    }

    @Test
    fun `lista com varios visitantes reconhece cada um`() {
        val lista = ListaAutorizados(visitantes = mutableSetOf(v01, v02))

        assertTrue(lista.contem(1L))
        assertTrue(lista.contem(2L))
    }
}
