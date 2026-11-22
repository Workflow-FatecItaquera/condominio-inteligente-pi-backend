package com.example.condominio.service

import com.example.condominio.model.Apartamento
import com.example.condominio.model.AutorizacaoAcesso
import com.example.condominio.model.Condominio
import com.example.condominio.model.ListaAutorizados
import com.example.condominio.model.RegistroAcesso
import com.example.condominio.model.Visitante
import com.example.condominio.repository.AutorizacaoAcessoRepository
import com.example.condominio.repository.ListaAutorizadosRepository
import com.example.condominio.repository.RegistroAcessoRepository
import com.example.condominio.repository.VisitanteRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.Optional

/**
 * Testes de unidade do caso de uso Controle de Acesso de Visitantes (RN01 a RN05).
 *
 * Recursos de teste usados (cada um identificado no código):
 *  - MOCK: os repositórios (VisitanteRepository, ListaAutorizadosRepository,
 *    AutorizacaoAcessoRepository, RegistroAcessoRepository) substituem o banco de dados.
 *  - STUB: os métodos de consulta dos mocks devolvem dados em memória montados em cada cenário.
 *  - STUB: CalendarioFalso substitui a consulta de feriados (RN02).
 *  - DRIVER: cada teste chama avaliarEntrada/registrarEntrada/registrarSaida passando o
 *    instante "agora" como parâmetro, o que dispensa um relógio de verdade.
 *
 * Massa-base B0 (dia D = 01/12/2026, terça-feira, dia útil e não feriado):
 *  - unidades U101 (id 101) e U102 (id 102); visitantes V01 (DOC-V01) e V02 (DOC-V02)
 *  - lista da U101 e da U102 atualizada em 01/09/2026, com V01 e V02
 *  - autorização A01: V01 e U101, de 26/11 a 15/12/2026, das 08:00 às 18:00,
 *    sem permissão de fim de semana nem de feriado
 */
class ControleAcessoServiceTest {

    // ---------- MOCKS ----------
    private val visitanteRepository = mock<VisitanteRepository>()
    private val listaRepository = mock<ListaAutorizadosRepository>()
    private val autorizacaoRepository = mock<AutorizacaoAcessoRepository>()
    private val registroRepository = mock<RegistroAcessoRepository>()
    private val falhas = mock<InjetorFalhas>() // MOCK: por padrão não induz falha nenhuma

    // ---------- STUB do calendário de feriados ----------
    private class CalendarioFalso(val feriados: MutableSet<LocalDate> = mutableSetOf()) : CalendarioFeriados {
        override fun ehFeriado(data: LocalDate): Boolean = feriados.contains(data)
    }

    private val calendario = CalendarioFalso()
    private val service = ControleAcessoService(
        visitanteRepository, listaRepository, autorizacaoRepository, registroRepository, calendario, falhas
    )

    // ---------- dados em memória ----------
    private val condominio = Condominio(id = 1L, nome = "C01", cnpj = "99999999000199")
    private val u101 = Apartamento(id = 101L, condominio = condominio, bloco = "A", numero = 101)
    private val u102 = Apartamento(id = 102L, condominio = condominio, bloco = "A", numero = 102)
    private val v01 = Visitante(id = 1L, nome = "V01", documento = "DOC-V01")
    private val v02 = Visitante(id = 2L, nome = "V02", documento = "DOC-V02")

    private val diaD = LocalDate.of(2026, 12, 1)
    private val tentativaPadrao = LocalDateTime.of(2026, 12, 1, 10, 0)

    private val visitantes = mutableListOf<Visitante>()
    private val autorizacoes = mutableListOf<AutorizacaoAcesso>()
    private val listas = mutableMapOf<Long, ListaAutorizados>()

    private fun autorizacaoA01() = AutorizacaoAcesso(
        id = 10L, visitante = v01, unidade = u101,
        dataInicio = LocalDate.of(2026, 11, 26), dataFim = LocalDate.of(2026, 12, 15),
        horaInicio = LocalTime.of(8, 0), horaFim = LocalTime.of(18, 0)
    )

    private fun lista(unidade: Apartamento, atualizadaEm: LocalDate, vararg membros: Visitante) =
        ListaAutorizados(id = unidade.id, unidade = unidade, atualizadaEm = atualizadaEm, visitantes = membros.toMutableSet())

    @BeforeEach
    fun prepararB0() {
        visitantes.clear(); autorizacoes.clear(); listas.clear()
        visitantes.addAll(listOf(v01, v02))
        autorizacoes.add(autorizacaoA01())
        listas[101L] = lista(u101, LocalDate.of(2026, 9, 1), v01, v02)
        listas[102L] = lista(u102, LocalDate.of(2026, 9, 1), v01, v02)

        // STUBS: os repositórios respondem a partir das listas em memória, que cada teste altera
        whenever(visitanteRepository.findById(any<Long>())).thenAnswer { inv ->
            Optional.ofNullable(visitantes.firstOrNull { it.id == inv.getArgument<Long>(0) })
        }
        whenever(visitanteRepository.findFirstByDocumento(any<String>())).thenAnswer { inv ->
            visitantes.firstOrNull { it.documento == inv.getArgument<String>(0) }
        }
        whenever(listaRepository.findFirstByUnidadeIdAndActiveTrueOrderByAtualizadaEmDescIdDesc(any<Long>())).thenAnswer { inv ->
            listas[inv.getArgument<Long>(0)]
        }
        whenever(autorizacaoRepository.findAllByVisitanteIdAndUnidadeId(any<Long>(), any<Long>())).thenAnswer { inv ->
            autorizacoes.filter { it.visitante.id == inv.getArgument<Long>(0) && it.unidade.id == inv.getArgument<Long>(1) }
        }
        whenever(autorizacaoRepository.findAllByVisitanteId(any<Long>())).thenAnswer { inv ->
            autorizacoes.filter { it.visitante.id == inv.getArgument<Long>(0) }
        }
        whenever(autorizacaoRepository.findAllByUnidadeId(any<Long>())).thenAnswer { inv ->
            autorizacoes.filter { it.unidade.id == inv.getArgument<Long>(0) }
        }
        whenever(autorizacaoRepository.findById(any<Long>())).thenAnswer { inv ->
            Optional.ofNullable(autorizacoes.firstOrNull { it.id == inv.getArgument<Long>(0) })
        }
        whenever(registroRepository.save(any<RegistroAcesso>())).thenAnswer { inv ->
            val registro = inv.getArgument<RegistroAcesso>(0)
            if (registro.id == null) registro.copy(id = 500L) else registro
        }
    }

    private fun avaliar(
        agora: LocalDateTime = tentativaPadrao,
        visitanteId: Long? = 1L,
        unidadeId: Long = 101L,
        documento: String = "DOC-V01"
    ) = service.avaliarEntrada(visitanteId, unidadeId, documento, agora)

    private fun assertNegada(decisao: DecisaoAcesso, motivo: MotivoNegacao) {
        assertFalse(decisao.permitido)
        assertEquals(motivo, decisao.motivo)
        assertNull(decisao.autorizacao)
    }

    // ================= Fluxo principal (C1, T5) =================

    @Test
    fun `fluxo principal permite a entrada quando todas as regras sao atendidas`() {
        val decisao = avaliar()

        assertTrue(decisao.permitido)
        assertNull(decisao.motivo)
        assertEquals(10L, decisao.autorizacao?.id)
    }

    @Test
    fun `visitante pode ser identificado apenas pelo documento`() {
        val decisao = avaliar(visitanteId = null)

        assertTrue(decisao.permitido)
    }

    // ================= RN01: cadastro, documento e lista =================

    @Test
    fun `RN01 nega quando o cadastro nao existe`() {
        assertNegada(avaliar(visitanteId = 99L), MotivoNegacao.CADASTRO_NAO_ENCONTRADO)
    }

    @Test
    fun `RN01 nega quando o documento informado nao existe e nao ha id`() {
        assertNegada(avaliar(visitanteId = null, documento = "DOC-INEXISTENTE"), MotivoNegacao.CADASTRO_NAO_ENCONTRADO)
    }

    @Test
    fun `RN01 nega quando o cadastro esta inativo`() {
        visitantes[0] = v01.copy(active = false)

        assertNegada(avaliar(), MotivoNegacao.CADASTRO_INATIVO)
    }

    @Test
    fun `RN01 nega quando o documento apresentado nao corresponde ao cadastro`() {
        assertNegada(avaliar(documento = "DOC-OUTRO"), MotivoNegacao.DOCUMENTO_DIVERGENTE)
    }

    @Test
    fun `RN01 compara o documento ignorando espacos nas pontas e caixa`() {
        assertTrue(avaliar(documento = "  doc-v01 ").permitido)
    }

    @Test
    fun `RN01 nega quando o visitante nao consta na lista da unidade`() {
        listas[101L] = lista(u101, LocalDate.of(2026, 9, 1), v02)

        assertNegada(avaliar(), MotivoNegacao.FORA_DA_LISTA)
    }

    @Test
    fun `RN01 nega quando a unidade nao tem lista`() {
        listas.remove(101L)

        assertNegada(avaliar(), MotivoNegacao.FORA_DA_LISTA)
    }

    @Test
    fun `RN01 permite no ultimo dia de validade da lista (6 meses exatos)`() {
        listas[101L] = lista(u101, diaD.minusMonths(6), v01)

        assertTrue(avaliar().permitido)
    }

    @Test
    fun `RN01 nega quando a lista venceu ha um dia (6 meses e 1 dia)`() {
        listas[101L] = lista(u101, diaD.minusMonths(6).minusDays(1), v01)

        assertNegada(avaliar(), MotivoNegacao.LISTA_VENCIDA)
    }

    @Test
    fun `RN01 revisar a lista vencida volta a permitir sem alterar a autorizacao`() {
        listas[101L] = lista(u101, diaD.minusMonths(7), v01)
        assertNegada(avaliar(), MotivoNegacao.LISTA_VENCIDA)

        listas[101L] = lista(u101, diaD, v01)

        assertTrue(avaliar().permitido)
    }

    // ================= RN03: vínculo =================

    @Test
    fun `RN03 nega quando a autorizacao pertence a outra unidade`() {
        assertNegada(avaliar(unidadeId = 102L), MotivoNegacao.AUTORIZACAO_OUTRA_UNIDADE)
    }

    @Test
    fun `RN03 nega quando a autorizacao da unidade pertence a outra pessoa`() {
        assertNegada(avaliar(visitanteId = 2L, documento = "DOC-V02"), MotivoNegacao.AUTORIZACAO_OUTRA_PESSOA)
    }

    @Test
    fun `RN03 nega quando nao existe nenhuma autorizacao`() {
        autorizacoes.clear()

        assertNegada(avaliar(), MotivoNegacao.SEM_AUTORIZACAO)
    }

    // ================= RN05: cancelamento =================

    @Test
    fun `RN05 nega autorizacao cancelada mesmo dentro do periodo de validade`() {
        autorizacoes[0] = autorizacaoA01().cancelar(tentativaPadrao.minusHours(1))

        assertNegada(avaliar(), MotivoNegacao.AUTORIZACAO_CANCELADA)
    }

    @Test
    fun `RN05 usa a autorizacao ativa quando existe outra cancelada para o mesmo par`() {
        autorizacoes.add(autorizacaoA01().copy(id = 11L).cancelar(tentativaPadrao.minusDays(2)))

        val decisao = avaliar()

        assertTrue(decisao.permitido)
        assertEquals(10L, decisao.autorizacao?.id)
    }

    @Test
    fun `RN05 cancelamento entre a consulta e a confirmacao nega e nao grava (C9)`() {
        val consulta = avaliar()
        assertTrue(consulta.permitido)

        // o responsável cancela depois da consulta e antes da confirmação
        autorizacoes[0] = autorizacaoA01().cancelar(tentativaPadrao.plusMinutes(1))
        val resultado = service.registrarEntrada(10L, 7L, "DOC-V01", tentativaPadrao.plusMinutes(2))

        assertNull(resultado.registro)
        assertEquals(MotivoNegacao.AUTORIZACAO_CANCELADA, resultado.decisao.motivo)
        verify(registroRepository, never()).save(any<RegistroAcesso>())
    }

    // ================= RN02: datas (valor limite) =================

    @ParameterizedTest
    @CsvSource(
        "2026-11-25T10:00:00, ANTES_DO_PERIODO",
        "2026-11-26T10:00:00, PERMITIDO",
        "2026-11-27T10:00:00, PERMITIDO",
        "2026-12-14T10:00:00, PERMITIDO",
        "2026-12-15T10:00:00, PERMITIDO",
        "2026-12-16T10:00:00, APOS_O_PERIODO"
    )
    fun `RN02 respeita a data inicial e a data final inclusivas`(agora: String, esperado: String) {
        val decisao = avaliar(agora = LocalDateTime.parse(agora))

        assertEquals(esperado, resumo(decisao))
    }

    // ================= RN02: horários (valor limite) =================

    @ParameterizedTest
    @CsvSource(
        "2026-12-01T07:59:59, FORA_DO_HORARIO",
        "2026-12-01T08:00:00, PERMITIDO",
        "2026-12-01T08:00:01, PERMITIDO",
        "2026-12-01T17:59:59, PERMITIDO",
        "2026-12-01T18:00:00, PERMITIDO",
        "2026-12-01T18:00:01, FORA_DO_HORARIO"
    )
    fun `RN02 respeita o horario inicial e o horario final inclusivos`(agora: String, esperado: String) {
        val decisao = avaliar(agora = LocalDateTime.parse(agora))

        assertEquals(esperado, resumo(decisao))
    }

    // ================= RN02: fim de semana e feriado =================

    @Test
    fun `RN02 nega sabado sem permissao de fim de semana`() {
        autorizacoes[0] = autorizacaoA01().copy(dataFim = LocalDate.of(2026, 12, 31))

        assertNegada(avaliar(agora = LocalDateTime.of(2026, 12, 5, 10, 0)), MotivoNegacao.FIM_DE_SEMANA_OU_FERIADO_SEM_PERMISSAO)
    }

    @Test
    fun `RN02 nega domingo sem permissao de fim de semana`() {
        autorizacoes[0] = autorizacaoA01().copy(dataFim = LocalDate.of(2026, 12, 31))

        assertNegada(avaliar(agora = LocalDateTime.of(2026, 12, 6, 10, 0)), MotivoNegacao.FIM_DE_SEMANA_OU_FERIADO_SEM_PERMISSAO)
    }

    @Test
    fun `RN02 permite sabado com permissao de fim de semana`() {
        autorizacoes[0] = autorizacaoA01().copy(dataFim = LocalDate.of(2026, 12, 31), permiteFimSemana = true)

        assertTrue(avaliar(agora = LocalDateTime.of(2026, 12, 5, 10, 0)).permitido)
    }

    @Test
    fun `RN02 nega feriado em dia util sem permissao de feriado`() {
        autorizacoes[0] = autorizacaoA01().copy(dataFim = LocalDate.of(2026, 12, 31))
        calendario.feriados.add(LocalDate.of(2026, 12, 25))

        assertNegada(avaliar(agora = LocalDateTime.of(2026, 12, 25, 10, 0)), MotivoNegacao.FIM_DE_SEMANA_OU_FERIADO_SEM_PERMISSAO)
    }

    @Test
    fun `RN02 permite feriado em dia util com permissao de feriado`() {
        autorizacoes[0] = autorizacaoA01().copy(dataFim = LocalDate.of(2026, 12, 31), permiteFeriado = true)
        calendario.feriados.add(LocalDate.of(2026, 12, 25))

        assertTrue(avaliar(agora = LocalDateTime.of(2026, 12, 25, 10, 0)).permitido)
    }

    @Test
    fun `RN02 sabado que tambem e feriado exige as duas permissoes`() {
        val sabadoFeriado = LocalDateTime.of(2026, 12, 5, 10, 0)
        calendario.feriados.add(sabadoFeriado.toLocalDate())

        autorizacoes[0] = autorizacaoA01().copy(dataFim = LocalDate.of(2026, 12, 31), permiteFimSemana = true)
        assertNegada(avaliar(agora = sabadoFeriado), MotivoNegacao.FIM_DE_SEMANA_OU_FERIADO_SEM_PERMISSAO)

        autorizacoes[0] = autorizacaoA01().copy(dataFim = LocalDate.of(2026, 12, 31), permiteFeriado = true)
        assertNegada(avaliar(agora = sabadoFeriado), MotivoNegacao.FIM_DE_SEMANA_OU_FERIADO_SEM_PERMISSAO)

        autorizacoes[0] = autorizacaoA01().copy(dataFim = LocalDate.of(2026, 12, 31), permiteFimSemana = true, permiteFeriado = true)
        assertTrue(avaliar(agora = sabadoFeriado).permitido)
    }

    // ================= RN01 e RN02: validade da lista na fronteira da data =================

    @ParameterizedTest
    @CsvSource(
        "2026-11-30T10:00:00, PERMITIDO",
        "2026-12-01T10:00:00, PERMITIDO",
        "2026-12-02T10:00:00, LISTA_VENCIDA"
    )
    fun `RN01 lista atualizada em 01-06-2026 vale ate 01-12-2026`(agora: String, esperado: String) {
        listas[101L] = lista(u101, LocalDate.of(2026, 6, 1), v01)
        autorizacoes[0] = autorizacaoA01().copy(dataInicio = LocalDate.of(2026, 11, 20), dataFim = LocalDate.of(2026, 12, 31))

        assertEquals(esperado, resumo(avaliar(agora = LocalDateTime.parse(agora))))
    }

    // ================= Serviço de consulta indisponível (C3) =================

    @Test
    fun `falha na consulta nega com SERVICO_INDISPONIVEL e nao libera`() {
        whenever(visitanteRepository.findById(any<Long>())).thenThrow(RuntimeException("banco indisponível"))

        assertNegada(avaliar(), MotivoNegacao.SERVICO_INDISPONIVEL)
    }

    @Test
    fun `falha induzida na consulta nega com SERVICO_INDISPONIVEL (C3)`() {
        whenever(falhas.verificar(PontoDeFalha.CONSULTA)).thenThrow(IllegalStateException("falha induzida"))

        assertNegada(avaliar(), MotivoNegacao.SERVICO_INDISPONIVEL)
    }

    @Test
    fun `falha induzida ao gravar a entrada nao confirma a liberacao (C10)`() {
        whenever(falhas.verificar(PontoDeFalha.GRAVAR_ENTRADA)).thenThrow(IllegalStateException("falha induzida"))

        val resultado = service.registrarEntrada(10L, 7L, "DOC-V01", tentativaPadrao)

        assertNull(resultado.registro)
        assertEquals(MotivoNegacao.FALHA_AO_GRAVAR, resultado.decisao.motivo)
        verify(registroRepository, never()).save(any<RegistroAcesso>())
    }

    // ================= RN04: registrar entrada =================

    @Test
    fun `RN04 registrarEntrada grava uma unica vez com unidade, porteiro e horario`() {
        val resultado = service.registrarEntrada(10L, 7L, "DOC-V01", tentativaPadrao)

        assertTrue(resultado.decisao.permitido)
        val registro = resultado.registro
        assertNotNull(registro)
        assertEquals(500L, registro!!.id)
        assertEquals(101L, registro.unidadeId)
        assertEquals(7L, registro.porteiroId)
        assertEquals(tentativaPadrao, registro.entradaEm)
        assertTrue(registro.visitaAberta())
        verify(registroRepository, times(1)).save(any<RegistroAcesso>())
    }

    @Test
    fun `RN04 entrada negada nao cria registro`() {
        autorizacoes[0] = autorizacaoA01().copy(dataInicio = LocalDate.of(2026, 12, 2))

        val resultado = service.registrarEntrada(10L, 7L, "DOC-V01", tentativaPadrao)

        assertNull(resultado.registro)
        assertEquals(MotivoNegacao.ANTES_DO_PERIODO, resultado.decisao.motivo)
        verify(registroRepository, never()).save(any<RegistroAcesso>())
    }

    @Test
    fun `RN04 autorizacao inexistente na confirmacao nao cria registro`() {
        val resultado = service.registrarEntrada(999L, 7L, "DOC-V01", tentativaPadrao)

        assertNull(resultado.registro)
        assertEquals(MotivoNegacao.SEM_AUTORIZACAO, resultado.decisao.motivo)
        verify(registroRepository, never()).save(any<RegistroAcesso>())
    }

    @Test
    fun `RN04 falha ao gravar a entrada nao confirma a liberacao (C10)`() {
        whenever(registroRepository.save(any<RegistroAcesso>())).thenThrow(RuntimeException("falha de gravação"))

        val resultado = service.registrarEntrada(10L, 7L, "DOC-V01", tentativaPadrao)

        assertNull(resultado.registro)
        assertFalse(resultado.decisao.permitido)
        assertEquals(MotivoNegacao.FALHA_AO_GRAVAR, resultado.decisao.motivo)
    }

    // ================= RN04: registrar saída =================

    @Test
    fun `RN04 registrarSaida atualiza o mesmo registro da entrada`() {
        val aberto = RegistroAcesso(id = 500L, autorizacao = autorizacaoA01(), unidadeId = 101L, porteiroId = 7L, entradaEm = tentativaPadrao)
        whenever(registroRepository.findById(500L)).thenReturn(Optional.of(aberto))
        val saida = tentativaPadrao.plusHours(2)
        val captor = argumentCaptor<RegistroAcesso>()

        val resultado = service.registrarSaida(500L, 8L, saida)

        assertEquals(StatusSaida.REGISTRADA, resultado.status)
        verify(registroRepository).save(captor.capture())
        assertEquals(500L, captor.firstValue.id)
        assertEquals(tentativaPadrao, captor.firstValue.entradaEm)
        assertEquals(saida, captor.firstValue.saidaEm)
        assertEquals(8L, captor.firstValue.porteiroSaidaId)
    }

    @Test
    fun `RN04 registrarSaida informa registro inexistente`() {
        whenever(registroRepository.findById(404L)).thenReturn(Optional.empty())

        assertEquals(StatusSaida.NAO_ENCONTRADO, service.registrarSaida(404L, 8L, tentativaPadrao).status)
        verify(registroRepository, never()).save(any<RegistroAcesso>())
    }

    @Test
    fun `RN04 registrarSaida nao grava duas vezes a mesma saida`() {
        val encerrado = RegistroAcesso(id = 500L, autorizacao = autorizacaoA01(), unidadeId = 101L, porteiroId = 7L,
            entradaEm = tentativaPadrao, saidaEm = tentativaPadrao.plusHours(1))
        whenever(registroRepository.findById(500L)).thenReturn(Optional.of(encerrado))

        assertEquals(StatusSaida.JA_ENCERRADA, service.registrarSaida(500L, 8L, tentativaPadrao.plusHours(3)).status)
        verify(registroRepository, never()).save(any<RegistroAcesso>())
    }

    @Test
    fun `RN04 falha ao gravar a saida informa o erro e mantem a visita aberta (C11)`() {
        val aberto = RegistroAcesso(id = 500L, autorizacao = autorizacaoA01(), unidadeId = 101L, porteiroId = 7L, entradaEm = tentativaPadrao)
        whenever(registroRepository.findById(500L)).thenReturn(Optional.of(aberto))
        whenever(registroRepository.save(any<RegistroAcesso>())).thenThrow(RuntimeException("falha de gravação"))

        val resultado = service.registrarSaida(500L, 8L, tentativaPadrao.plusHours(1))

        assertEquals(StatusSaida.FALHA_AO_GRAVAR, resultado.status)
        assertTrue(aberto.visitaAberta())
    }

    @Test
    fun `falha induzida ao gravar a saida mantem a visita aberta (C11)`() {
        val aberto = RegistroAcesso(id = 500L, autorizacao = autorizacaoA01(), unidadeId = 101L, porteiroId = 7L, entradaEm = tentativaPadrao)
        whenever(registroRepository.findById(500L)).thenReturn(Optional.of(aberto))
        whenever(falhas.verificar(PontoDeFalha.GRAVAR_SAIDA)).thenThrow(IllegalStateException("falha induzida"))

        val resultado = service.registrarSaida(500L, 8L, tentativaPadrao.plusHours(1))

        assertEquals(StatusSaida.FALHA_AO_GRAVAR, resultado.status)
        verify(registroRepository, never()).save(any<RegistroAcesso>())
    }

    @Test
    fun `RN04 e RN05 a saida continua permitida depois do cancelamento e a reentrada e negada (S3)`() {
        val entrada = service.registrarEntrada(10L, 7L, "DOC-V01", tentativaPadrao)
        val registro = entrada.registro!!
        autorizacoes[0] = autorizacaoA01().cancelar(tentativaPadrao.plusMinutes(30))
        whenever(registroRepository.findById(500L)).thenReturn(Optional.of(registro))

        val saida = service.registrarSaida(500L, 8L, tentativaPadrao.plusHours(1))
        val reentrada = service.avaliarEntrada(1L, 101L, "DOC-V01", tentativaPadrao.plusHours(2))

        assertEquals(StatusSaida.REGISTRADA, saida.status)
        assertNegada(reentrada, MotivoNegacao.AUTORIZACAO_CANCELADA)
    }

    // ================= Consultas =================

    @Test
    fun `lerAbertos e lerRegistro delegam ao repositorio`() {
        val aberto = RegistroAcesso(id = 500L, autorizacao = autorizacaoA01(), unidadeId = 101L, porteiroId = 7L, entradaEm = tentativaPadrao)
        whenever(registroRepository.findAllBySaidaEmIsNull()).thenReturn(listOf(aberto))
        whenever(registroRepository.findAll()).thenReturn(listOf(aberto))
        whenever(registroRepository.findById(500L)).thenReturn(Optional.of(aberto))
        whenever(registroRepository.findById(1L)).thenReturn(Optional.empty())

        assertEquals(listOf(aberto), service.lerAbertos())
        assertEquals(listOf(aberto), service.lerRegistros())
        assertEquals(aberto, service.lerRegistro(500L))
        assertNull(service.lerRegistro(1L))
    }

    private fun resumo(decisao: DecisaoAcesso): String {
        return if (decisao.permitido) "PERMITIDO" else decisao.motivo!!.name
    }
}
