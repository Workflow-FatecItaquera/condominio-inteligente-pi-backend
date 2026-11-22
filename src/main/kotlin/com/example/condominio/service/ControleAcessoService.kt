package com.example.condominio.service

import com.example.condominio.model.AutorizacaoAcesso
import com.example.condominio.model.RegistroAcesso
import com.example.condominio.model.Visitante
import com.example.condominio.repository.AutorizacaoAcessoRepository
import com.example.condominio.repository.ListaAutorizadosRepository
import com.example.condominio.repository.RegistroAcessoRepository
import com.example.condominio.repository.VisitanteRepository
import org.springframework.stereotype.Service
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Caso de uso "Controle de Acesso de Visitantes" (RN01 a RN05).
 *
 * Premissas adotadas (registradas no Quadro 1a):
 *  - RN01: "6 meses" são meses de calendário e o último dia é inclusivo
 *    (lista atualizada em 11/05 vale até 11/11).
 *  - RN02: data inicial/final e hora inicial/final são inclusivas.
 *  - RN05: a saída continua permitida depois do cancelamento (quem entrou precisa sair).
 */
@Service
class ControleAcessoService(
    private val visitanteRepository: VisitanteRepository,
    private val listaRepository: ListaAutorizadosRepository,
    private val autorizacaoRepository: AutorizacaoAcessoRepository,
    private val registroRepository: RegistroAcessoRepository,
    private val calendario: CalendarioFeriados,
    private val falhas: InjetorFalhas
) {

    /**
     * Passos 2 a 4 do fluxo principal: consulta e avaliação das regras.
     * Se a consulta falhar, nenhuma liberação é feita (caminho C3).
     */
    fun avaliarEntrada(
        visitanteId: Long?,
        unidadeId: Long,
        documento: String,
        agora: LocalDateTime
    ): DecisaoAcesso {
        return try {
            aplicarRegras(visitanteId, unidadeId, documento, agora)
        } catch (e: Exception) {
            DecisaoAcesso.negada(
                MotivoNegacao.SERVICO_INDISPONIVEL,
                "Serviço de consulta indisponível. Nenhuma liberação foi feita."
            )
        }
    }

    /**
     * Passos 5 e 6: a portaria confirma a liberação. As condições são
     * revalidadas no momento da confirmação (RN05 pode ter mudado desde a consulta)
     * e só então a entrada é gravada (RN04).
     */
    fun registrarEntrada(
        autorizacaoId: Long,
        porteiroId: Long,
        documento: String,
        agora: LocalDateTime
    ): ResultadoEntrada {
        val autorizacao = autorizacaoRepository.findById(autorizacaoId).orElse(null)
        if (autorizacao == null) {
            return ResultadoEntrada(
                DecisaoAcesso.negada(MotivoNegacao.SEM_AUTORIZACAO, "Autorização não encontrada")
            )
        }

        val decisao = avaliarEntrada(autorizacao.visitante.id, autorizacao.unidade.id!!, documento, agora)
        if (!decisao.permitido) {
            return ResultadoEntrada(decisao)
        }

        return try {
            falhas.verificar(PontoDeFalha.GRAVAR_ENTRADA)
            val liberada = decisao.autorizacao!!
            val registro = registroRepository.save(
                RegistroAcesso(
                    autorizacao = liberada,
                    unidadeId = liberada.unidade.id!!,
                    porteiroId = porteiroId,
                    entradaEm = agora
                )
            )
            ResultadoEntrada(decisao, registro)
        } catch (e: Exception) {
            ResultadoEntrada(
                DecisaoAcesso.negada(
                    MotivoNegacao.FALHA_AO_GRAVAR,
                    "Falha ao gravar a entrada. A liberação não foi confirmada."
                )
            )
        }
    }

    /** Passos 7 e 8: a saída é gravada no mesmo registro da entrada (RN04). */
    fun registrarSaida(registroId: Long, porteiroId: Long, agora: LocalDateTime): ResultadoSaida {
        val registro = registroRepository.findById(registroId).orElse(null)
        if (registro == null) {
            return ResultadoSaida(StatusSaida.NAO_ENCONTRADO)
        }
        if (!registro.visitaAberta()) {
            return ResultadoSaida(StatusSaida.JA_ENCERRADA, registro)
        }
        return try {
            falhas.verificar(PontoDeFalha.GRAVAR_SAIDA)
            val salvo = registroRepository.save(registro.registrarSaida(agora, porteiroId))
            ResultadoSaida(StatusSaida.REGISTRADA, salvo)
        } catch (e: Exception) {
            ResultadoSaida(StatusSaida.FALHA_AO_GRAVAR, registro)
        }
    }

    fun lerRegistros(): List<RegistroAcesso> {
        return registroRepository.findAll()
    }

    fun lerAbertos(): List<RegistroAcesso> {
        return registroRepository.findAllBySaidaEmIsNull()
    }

    fun lerRegistro(id: Long): RegistroAcesso? {
        return registroRepository.findById(id).orElse(null)
    }

    // ------------------------------------------------------------------
    // Regras. Cada condição é um if simples para que o grafo de fluxo de
    // controle (Exercício 8) tenha um predicado por decisão.
    // ------------------------------------------------------------------
    private fun aplicarRegras(
        visitanteId: Long?,
        unidadeId: Long,
        documento: String,
        agora: LocalDateTime
    ): DecisaoAcesso {
        falhas.verificar(PontoDeFalha.CONSULTA)

        // RN01: cadastro ativo e documento correspondente
        val visitante = localizarVisitante(visitanteId, documento)
        if (visitante == null) {
            return DecisaoAcesso.negada(MotivoNegacao.CADASTRO_NAO_ENCONTRADO, "Cadastro do visitante não encontrado")
        }
        if (!visitante.active) {
            return DecisaoAcesso.negada(MotivoNegacao.CADASTRO_INATIVO, "Cadastro do visitante inativo")
        }
        if (!visitante.documento.equals(documento.trim(), ignoreCase = true)) {
            return DecisaoAcesso.negada(MotivoNegacao.DOCUMENTO_DIVERGENTE, "Documento não corresponde ao cadastro")
        }

        // RN01: lista da unidade atualizada nos últimos 6 meses e com o visitante
        val data = agora.toLocalDate()
        val lista = listaRepository.findFirstByUnidadeIdAndActiveTrueOrderByAtualizadaEmDescIdDesc(unidadeId)
        if (lista == null) {
            return DecisaoAcesso.negada(MotivoNegacao.FORA_DA_LISTA, "A unidade não possui lista de autorizados")
        }
        if (!lista.vigenteEm(data)) {
            return DecisaoAcesso.negada(MotivoNegacao.LISTA_VENCIDA, "Lista da unidade vencida (mais de 6 meses)")
        }
        if (!lista.contem(visitante.id)) {
            return DecisaoAcesso.negada(MotivoNegacao.FORA_DA_LISTA, "Visitante não consta na lista da unidade")
        }

        // RN03: autorização vinculada ao visitante e à unidade
        val visitanteReal = visitante.id!!
        val vinculadas = autorizacaoRepository.findAllByVisitanteIdAndUnidadeId(visitanteReal, unidadeId)
        if (vinculadas.isEmpty()) {
            if (autorizacaoRepository.findAllByVisitanteId(visitanteReal).isNotEmpty()) {
                return DecisaoAcesso.negada(
                    MotivoNegacao.AUTORIZACAO_OUTRA_UNIDADE,
                    "A autorização do visitante pertence a outra unidade"
                )
            }
            if (autorizacaoRepository.findAllByUnidadeId(unidadeId).isNotEmpty()) {
                return DecisaoAcesso.negada(
                    MotivoNegacao.AUTORIZACAO_OUTRA_PESSOA,
                    "A autorização da unidade pertence a outra pessoa"
                )
            }
            return DecisaoAcesso.negada(MotivoNegacao.SEM_AUTORIZACAO, "Não há autorização para o visitante nesta unidade")
        }

        // RN05: autorização cancelada nega mesmo dentro da validade
        val escolhida = selecionarAutorizacao(vinculadas, data)
        if (escolhida == null) {
            return DecisaoAcesso.negada(MotivoNegacao.AUTORIZACAO_CANCELADA, "Autorização cancelada pelo responsável")
        }

        // RN02: datas e horários permitidos
        if (data.isBefore(escolhida.dataInicio)) {
            return DecisaoAcesso.negada(MotivoNegacao.ANTES_DO_PERIODO, "Data anterior ao início da autorização")
        }
        if (data.isAfter(escolhida.dataFim)) {
            return DecisaoAcesso.negada(MotivoNegacao.APOS_O_PERIODO, "Data posterior ao fim da autorização")
        }
        if (!escolhida.dentroDoHorario(agora.toLocalTime())) {
            return DecisaoAcesso.negada(MotivoNegacao.FORA_DO_HORARIO, "Horário fora da janela autorizada")
        }

        // RN02: fim de semana e feriado exigem autorização expressa
        if (ehDiaEspecial(data)) {
            if (!diaEspecialAutorizado(escolhida, data)) {
                return DecisaoAcesso.negada(
                    MotivoNegacao.FIM_DE_SEMANA_OU_FERIADO_SEM_PERMISSAO,
                    "Fim de semana ou feriado sem autorização expressa"
                )
            }
        }

        return DecisaoAcesso.permitida(escolhida)
    }

    private fun localizarVisitante(visitanteId: Long?, documento: String): Visitante? {
        if (visitanteId != null) {
            return visitanteRepository.findById(visitanteId).orElse(null)
        }
        return visitanteRepository.findFirstByDocumento(documento.trim())
    }

    /**
     * Entre as autorizações do par visitante/unidade, ignora as canceladas e
     * prefere a que cobre a data; sem nenhuma que cubra, usa a de maior data final
     * (para informar se a data é anterior ou posterior). Retorna null se todas
     * estiverem canceladas.
     */
    private fun selecionarAutorizacao(candidatas: List<AutorizacaoAcesso>, data: LocalDate): AutorizacaoAcesso? {
        val ativas = candidatas.filter { !it.estaCancelada() }
        return ativas.firstOrNull { it.cobreData(data) } ?: ativas.maxByOrNull { it.dataFim }
    }

    private fun ehFimDeSemana(data: LocalDate): Boolean {
        return data.dayOfWeek == DayOfWeek.SATURDAY || data.dayOfWeek == DayOfWeek.SUNDAY
    }

    private fun ehDiaEspecial(data: LocalDate): Boolean {
        return ehFimDeSemana(data) || calendario.ehFeriado(data)
    }

    /** Sábado ou domingo exigem permiteFimSemana; feriado exige permiteFeriado (os dois, se coincidirem). */
    private fun diaEspecialAutorizado(autorizacao: AutorizacaoAcesso, data: LocalDate): Boolean {
        if (ehFimDeSemana(data) && !autorizacao.permiteFimSemana) {
            return false
        }
        if (calendario.ehFeriado(data) && !autorizacao.permiteFeriado) {
            return false
        }
        return true
    }
}
