package com.example.condominio.mapper

import com.example.condominio.dto.AutorizacaoDTOResponse
import com.example.condominio.dto.DecisaoAcessoDTOResponse
import com.example.condominio.dto.FeriadoDTOResponse
import com.example.condominio.dto.ListaAutorizadosDTOResponse
import com.example.condominio.dto.RegistroAcessoDTOResponse
import com.example.condominio.model.AutorizacaoAcesso
import com.example.condominio.model.Feriado
import com.example.condominio.model.ListaAutorizados
import com.example.condominio.model.RegistroAcesso
import com.example.condominio.service.DecisaoAcesso
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class ControleAcessoMapper {

    fun toResponse(decisao: DecisaoAcesso): DecisaoAcessoDTOResponse {
        return DecisaoAcessoDTOResponse(
            permitido = decisao.permitido,
            motivo = decisao.motivo?.name,
            mensagem = decisao.mensagem,
            autorizacaoId = decisao.autorizacao?.id
        )
    }

    fun toResponse(registro: RegistroAcesso): RegistroAcessoDTOResponse {
        return RegistroAcessoDTOResponse(
            id = registro.id ?: 0,
            autorizacaoId = registro.autorizacao.id ?: 0,
            visitanteId = registro.autorizacao.visitante.id ?: 0,
            visitanteNome = registro.autorizacao.visitante.nome,
            unidadeId = registro.unidadeId,
            porteiroId = registro.porteiroId,
            entradaEm = registro.entradaEm,
            saidaEm = registro.saidaEm,
            porteiroSaidaId = registro.porteiroSaidaId
        )
    }

    fun toResponse(autorizacao: AutorizacaoAcesso, agora: LocalDateTime): AutorizacaoDTOResponse {
        return AutorizacaoDTOResponse(
            id = autorizacao.id ?: 0,
            visitanteId = autorizacao.visitante.id ?: 0,
            unidadeId = autorizacao.unidade.id ?: 0,
            responsavelId = autorizacao.responsavel?.id,
            dataInicio = autorizacao.dataInicio,
            dataFim = autorizacao.dataFim,
            horaInicio = autorizacao.horaInicio,
            horaFim = autorizacao.horaFim,
            permiteFimSemana = autorizacao.permiteFimSemana,
            permiteFeriado = autorizacao.permiteFeriado,
            canceladaEm = autorizacao.canceladaEm,
            estado = autorizacao.estadoEm(agora).name
        )
    }

    fun toResponse(lista: ListaAutorizados): ListaAutorizadosDTOResponse {
        return ListaAutorizadosDTOResponse(
            id = lista.id ?: 0,
            unidadeId = lista.unidade.id ?: 0,
            atualizadaEm = lista.atualizadaEm,
            validaAte = lista.validaAte(),
            visitanteIds = lista.visitantes.mapNotNull { it.id }.sorted()
        )
    }

    fun toResponse(feriado: Feriado): FeriadoDTOResponse {
        return FeriadoDTOResponse(id = feriado.id ?: 0, data = feriado.data, nome = feriado.nome)
    }
}
