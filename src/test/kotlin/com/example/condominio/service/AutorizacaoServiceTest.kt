package com.example.condominio.service

import com.example.condominio.model.Apartamento
import com.example.condominio.model.AutorizacaoAcesso
import com.example.condominio.model.ListaAutorizados
import com.example.condominio.model.Visitante
import com.example.condominio.repository.ApartamentoRepository
import com.example.condominio.repository.AutorizacaoAcessoRepository
import com.example.condominio.repository.ListaAutorizadosRepository
import com.example.condominio.repository.UsuarioRepository
import com.example.condominio.repository.VisitanteRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.Optional

/**
 * Manutenção de autorizações e listas (RN01, RN02, RN03, RN05).
 * MOCK: repositórios; STUB: respostas de cada repositório em cada cenário.
 */
class AutorizacaoServiceTest {

    private val autorizacaoRepository = mock<AutorizacaoAcessoRepository>()
    private val listaRepository = mock<ListaAutorizadosRepository>()
    private val visitanteRepository = mock<VisitanteRepository>()
    private val apartamentoRepository = mock<ApartamentoRepository>()
    private val usuarioRepository = mock<UsuarioRepository>()
    private val service = AutorizacaoService(
        autorizacaoRepository, listaRepository, visitanteRepository, apartamentoRepository, usuarioRepository
    )

    private val v01 = Visitante(id = 1L, nome = "V01", documento = "DOC-V01")
    private val u101 = Apartamento(id = 101L)
    private val inicio = LocalDate.of(2026, 12, 1)
    private val fim = LocalDate.of(2026, 12, 15)

    private fun conceder(
        dataInicio: LocalDate = inicio,
        dataFim: LocalDate = fim,
        horaInicio: LocalTime = LocalTime.of(8, 0),
        horaFim: LocalTime = LocalTime.of(18, 0)
    ) = service.conceder(1L, 101L, null, dataInicio, dataFim, horaInicio, horaFim, false, true)

    @Test
    fun `conceder salva a autorizacao vinculada ao visitante e a unidade (RN03)`() {
        whenever(visitanteRepository.findByIdAndActiveTrue(1L)).thenReturn(v01)
        whenever(apartamentoRepository.findByIdAndActiveTrue(101L)).thenReturn(u101)
        whenever(autorizacaoRepository.save(any<AutorizacaoAcesso>())).thenAnswer { it.getArgument<AutorizacaoAcesso>(0) }
        val captor = argumentCaptor<AutorizacaoAcesso>()

        val resultado = conceder()

        assertNotNull(resultado)
        verify(autorizacaoRepository).save(captor.capture())
        assertEquals(v01, captor.firstValue.visitante)
        assertEquals(u101, captor.firstValue.unidade)
        assertEquals(inicio, captor.firstValue.dataInicio)
        assertEquals(fim, captor.firstValue.dataFim)
        assertFalse(captor.firstValue.permiteFimSemana)
        assertTrue(captor.firstValue.permiteFeriado)
    }

    @Test
    fun `conceder devolve null quando o visitante nao existe`() {
        whenever(visitanteRepository.findByIdAndActiveTrue(1L)).thenReturn(null)
        whenever(apartamentoRepository.findByIdAndActiveTrue(101L)).thenReturn(u101)

        assertNull(conceder())
        verify(autorizacaoRepository, never()).save(any<AutorizacaoAcesso>())
    }

    @Test
    fun `conceder devolve null quando a unidade nao existe`() {
        whenever(visitanteRepository.findByIdAndActiveTrue(1L)).thenReturn(v01)
        whenever(apartamentoRepository.findByIdAndActiveTrue(101L)).thenReturn(null)

        assertNull(conceder())
    }

    @Test
    fun `conceder rejeita data final anterior a data inicial (RN02)`() {
        assertThrows(IllegalArgumentException::class.java) { conceder(dataInicio = fim, dataFim = inicio) }
        verify(autorizacaoRepository, never()).save(any<AutorizacaoAcesso>())
    }

    @Test
    fun `conceder rejeita horario final anterior ao horario inicial (RN02)`() {
        assertThrows(IllegalArgumentException::class.java) {
            conceder(horaInicio = LocalTime.of(18, 0), horaFim = LocalTime.of(8, 0))
        }
    }

    @Test
    fun `cancelar grava o instante do cancelamento (RN05)`() {
        val agora = LocalDateTime.of(2026, 12, 2, 9, 0)
        whenever(autorizacaoRepository.findById(10L)).thenReturn(Optional.of(AutorizacaoAcesso(id = 10L, visitante = v01, unidade = u101)))
        val captor = argumentCaptor<AutorizacaoAcesso>()

        assertTrue(service.cancelar(10L, agora))

        verify(autorizacaoRepository).save(captor.capture())
        assertEquals(agora, captor.firstValue.canceladaEm)
    }

    @Test
    fun `cancelar de novo mantem o primeiro cancelamento`() {
        val primeira = LocalDateTime.of(2026, 12, 2, 9, 0)
        val cancelada = AutorizacaoAcesso(id = 10L, visitante = v01, unidade = u101, canceladaEm = primeira)
        whenever(autorizacaoRepository.findById(10L)).thenReturn(Optional.of(cancelada))

        assertTrue(service.cancelar(10L, primeira.plusDays(1)))

        verify(autorizacaoRepository, never()).save(any<AutorizacaoAcesso>())
    }

    @Test
    fun `cancelar devolve false quando a autorizacao nao existe`() {
        whenever(autorizacaoRepository.findById(404L)).thenReturn(Optional.empty())

        assertFalse(service.cancelar(404L, LocalDateTime.now()))
    }

    @Test
    fun `revisarLista cria a lista com os visitantes ativos e a data informada (RN01)`() {
        whenever(apartamentoRepository.findByIdAndActiveTrue(101L)).thenReturn(u101)
        whenever(visitanteRepository.findByIdAndActiveTrue(1L)).thenReturn(v01)
        whenever(visitanteRepository.findByIdAndActiveTrue(2L)).thenReturn(null) // inexistente ou inativo é ignorado
        whenever(listaRepository.save(any<ListaAutorizados>())).thenAnswer { it.getArgument<ListaAutorizados>(0) }

        val lista = service.revisarLista(101L, listOf(1L, 2L), null, inicio)

        assertNotNull(lista)
        assertEquals(inicio, lista!!.atualizadaEm)
        assertEquals(setOf(v01), lista.visitantes)
        assertEquals(LocalDate.of(2027, 6, 1), lista.validaAte())
    }

    @Test
    fun `revisarLista devolve null quando a unidade nao existe`() {
        whenever(apartamentoRepository.findByIdAndActiveTrue(999L)).thenReturn(null)

        assertNull(service.revisarLista(999L, listOf(1L), null, inicio))
        verify(listaRepository, never()).save(any<ListaAutorizados>())
    }

    @Test
    fun `consultas delegam aos repositorios`() {
        val autorizacao = AutorizacaoAcesso(id = 10L, visitante = v01, unidade = u101)
        val lista = ListaAutorizados(id = 3L, unidade = u101)
        whenever(autorizacaoRepository.findById(10L)).thenReturn(Optional.of(autorizacao))
        whenever(autorizacaoRepository.findAll()).thenReturn(listOf(autorizacao))
        whenever(listaRepository.findFirstByUnidadeIdAndActiveTrueOrderByAtualizadaEmDescIdDesc(101L)).thenReturn(lista)

        assertEquals(autorizacao, service.lerAutorizacao(10L))
        assertEquals(listOf(autorizacao), service.lerAutorizacoes())
        assertEquals(lista, service.lerListaVigente(101L))
    }
}
