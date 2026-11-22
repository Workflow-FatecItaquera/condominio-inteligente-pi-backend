package com.example.condominio.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/** RN02 e RN05: estados da autorização (T1 a T4) e limites de data e hora. */
class AutorizacaoAcessoTest {

    private val autorizacao = AutorizacaoAcesso(
        id = 1L,
        dataInicio = LocalDate.of(2026, 12, 1),
        dataFim = LocalDate.of(2026, 12, 15),
        horaInicio = LocalTime.of(8, 0, 0),
        horaFim = LocalTime.of(18, 0, 0)
    )

    @Test
    fun `antes da data inicial a autorizacao esta AGENDADA`() {
        assertEquals(EstadoAutorizacao.AGENDADA, autorizacao.estadoEm(LocalDateTime.of(2026, 11, 30, 10, 0)))
    }

    @Test
    fun `entre as datas a autorizacao esta VIGENTE incluindo os dois extremos`() {
        assertEquals(EstadoAutorizacao.VIGENTE, autorizacao.estadoEm(LocalDateTime.of(2026, 12, 1, 0, 0)))
        assertEquals(EstadoAutorizacao.VIGENTE, autorizacao.estadoEm(LocalDateTime.of(2026, 12, 15, 23, 59)))
    }

    @Test
    fun `depois da data final a autorizacao esta EXPIRADA`() {
        assertEquals(EstadoAutorizacao.EXPIRADA, autorizacao.estadoEm(LocalDateTime.of(2026, 12, 16, 0, 0)))
    }

    @Test
    fun `cancelar leva ao estado CANCELADA em qualquer fase e registra o instante`() {
        val instante = LocalDateTime.of(2026, 12, 5, 9, 0)

        val cancelada = autorizacao.cancelar(instante)

        assertTrue(cancelada.estaCancelada())
        assertEquals(instante, cancelada.canceladaEm)
        assertEquals(EstadoAutorizacao.CANCELADA, cancelada.estadoEm(LocalDateTime.of(2026, 12, 5, 10, 0)))
        assertEquals(EstadoAutorizacao.CANCELADA, cancelada.estadoEm(LocalDateTime.of(2026, 11, 20, 10, 0)))
        assertEquals(EstadoAutorizacao.CANCELADA, cancelada.estadoEm(LocalDateTime.of(2026, 12, 20, 10, 0)))
    }

    @Test
    fun `autorizacao nova nao esta cancelada`() {
        assertFalse(autorizacao.estaCancelada())
        assertNotNull(autorizacao.cancelar(LocalDateTime.now()).canceladaEm)
    }

    @ParameterizedTest
    @CsvSource(
        "2026-11-30, false",
        "2026-12-01, true",
        "2026-12-15, true",
        "2026-12-16, false"
    )
    fun `cobreData respeita os limites inclusivos`(data: String, esperado: Boolean) {
        assertEquals(esperado, autorizacao.cobreData(LocalDate.parse(data)))
    }

    @ParameterizedTest
    @CsvSource(
        "07:59:59, false",
        "08:00:00, true",
        "08:00:01, true",
        "17:59:59, true",
        "18:00:00, true",
        "18:00:01, false"
    )
    fun `dentroDoHorario respeita a janela diaria inclusiva`(hora: String, esperado: Boolean) {
        assertEquals(esperado, autorizacao.dentroDoHorario(LocalTime.parse(hora)))
    }
}
