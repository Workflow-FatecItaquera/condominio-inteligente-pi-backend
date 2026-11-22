package com.example.condominio.service

import com.example.condominio.model.AutorizacaoAcesso
import com.example.condominio.model.ListaAutorizados
import com.example.condominio.repository.ApartamentoRepository
import com.example.condominio.repository.AutorizacaoAcessoRepository
import com.example.condominio.repository.ListaAutorizadosRepository
import com.example.condominio.repository.UsuarioRepository
import com.example.condominio.repository.VisitanteRepository
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * Manutenção das autorizações (RN02, RN03, RN05) e da lista de visitantes
 * autorizados da unidade (RN01).
 */
@Service
class AutorizacaoService(
    private val autorizacaoRepository: AutorizacaoAcessoRepository,
    private val listaRepository: ListaAutorizadosRepository,
    private val visitanteRepository: VisitanteRepository,
    private val apartamentoRepository: ApartamentoRepository,
    private val usuarioRepository: UsuarioRepository
) {

    /**
     * Concede a autorização vinculada a um visitante e a uma unidade (RN03).
     * Devolve null se o visitante ou a unidade não existirem (ou estiverem inativos).
     * Lança IllegalArgumentException se a data ou a hora final for anterior à inicial (RN02).
     */
    fun conceder(
        visitanteId: Long,
        unidadeId: Long,
        responsavelId: Long?,
        dataInicio: LocalDate,
        dataFim: LocalDate,
        horaInicio: LocalTime,
        horaFim: LocalTime,
        permiteFimSemana: Boolean,
        permiteFeriado: Boolean
    ): AutorizacaoAcesso? {
        require(!dataFim.isBefore(dataInicio)) { "A data final não pode ser anterior à data inicial" }
        require(!horaFim.isBefore(horaInicio)) { "O horário final não pode ser anterior ao horário inicial" }

        val visitante = visitanteRepository.findByIdAndActiveTrue(visitanteId) ?: return null
        val unidade = apartamentoRepository.findByIdAndActiveTrue(unidadeId) ?: return null
        val responsavel = responsavelId?.let { usuarioRepository.findByIdAndActiveTrue(it) }

        return autorizacaoRepository.save(
            AutorizacaoAcesso(
                visitante = visitante,
                unidade = unidade,
                responsavel = responsavel,
                dataInicio = dataInicio,
                dataFim = dataFim,
                horaInicio = horaInicio,
                horaFim = horaFim,
                permiteFimSemana = permiteFimSemana,
                permiteFeriado = permiteFeriado
            )
        )
    }

    /**
     * RN05: o responsável pode cancelar a qualquer momento. Cancelar de novo mantém
     * o primeiro cancelamento. Devolve false se a autorização não existe.
     */
    fun cancelar(autorizacaoId: Long, agora: LocalDateTime): Boolean {
        val autorizacao = autorizacaoRepository.findById(autorizacaoId).orElse(null) ?: return false
        if (autorizacao.estaCancelada()) {
            return true
        }
        autorizacaoRepository.save(autorizacao.cancelar(agora))
        return true
    }

    /**
     * RN01: cria uma nova versão da lista da unidade com os visitantes ativos informados
     * (inexistentes ou inativos são ignorados). Devolve null se a unidade não existe.
     */
    fun revisarLista(
        unidadeId: Long,
        visitanteIds: List<Long>,
        responsavelId: Long?,
        atualizadaEm: LocalDate
    ): ListaAutorizados? {
        val unidade = apartamentoRepository.findByIdAndActiveTrue(unidadeId) ?: return null
        val visitantes = visitanteIds
            .mapNotNull { visitanteRepository.findByIdAndActiveTrue(it) }
            .toMutableSet()
        val responsavel = responsavelId?.let { usuarioRepository.findByIdAndActiveTrue(it) }

        return listaRepository.save(
            ListaAutorizados(
                unidade = unidade,
                responsavel = responsavel,
                atualizadaEm = atualizadaEm,
                visitantes = visitantes
            )
        )
    }

    fun lerAutorizacao(id: Long): AutorizacaoAcesso? {
        return autorizacaoRepository.findById(id).orElse(null)
    }

    fun lerAutorizacoes(): List<AutorizacaoAcesso> {
        return autorizacaoRepository.findAll()
    }

    fun lerListaVigente(unidadeId: Long): ListaAutorizados? {
        return listaRepository.findFirstByUnidadeIdAndActiveTrueOrderByAtualizadaEmDescIdDesc(unidadeId)
    }
}
