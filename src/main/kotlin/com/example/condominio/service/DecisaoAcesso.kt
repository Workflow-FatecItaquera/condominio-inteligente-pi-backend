package com.example.condominio.service

import com.example.condominio.model.AutorizacaoAcesso
import com.example.condominio.model.RegistroAcesso

/** Motivos pelos quais a entrada de um visitante é negada (RN01 a RN05 e falhas técnicas). */
enum class MotivoNegacao {
    CADASTRO_NAO_ENCONTRADO,
    CADASTRO_INATIVO,
    DOCUMENTO_DIVERGENTE,
    FORA_DA_LISTA,
    LISTA_VENCIDA,
    AUTORIZACAO_OUTRA_UNIDADE,
    AUTORIZACAO_OUTRA_PESSOA,
    SEM_AUTORIZACAO,
    AUTORIZACAO_CANCELADA,
    ANTES_DO_PERIODO,
    APOS_O_PERIODO,
    FORA_DO_HORARIO,
    FIM_DE_SEMANA_OU_FERIADO_SEM_PERMISSAO,
    SERVICO_INDISPONIVEL,
    FALHA_AO_GRAVAR
}

/** Resultado da avaliação das regras: entrada permitida (com a autorização usada) ou negada (com o motivo). */
data class DecisaoAcesso(
    val permitido: Boolean,
    val motivo: MotivoNegacao? = null,
    val mensagem: String = "",
    val autorizacao: AutorizacaoAcesso? = null
) {
    companion object {
        fun permitida(autorizacao: AutorizacaoAcesso): DecisaoAcesso {
            return DecisaoAcesso(true, null, "Entrada permitida", autorizacao)
        }

        fun negada(motivo: MotivoNegacao, mensagem: String): DecisaoAcesso {
            return DecisaoAcesso(false, motivo, mensagem, null)
        }
    }
}

/** Resultado de registrarEntrada: a decisão e, se a entrada foi gravada, o registro criado. */
data class ResultadoEntrada(
    val decisao: DecisaoAcesso,
    val registro: RegistroAcesso? = null
)

enum class StatusSaida {
    REGISTRADA,
    NAO_ENCONTRADO,
    JA_ENCERRADA,
    FALHA_AO_GRAVAR
}

/** Resultado de registrarSaida: o status e o registro (quando existe). */
data class ResultadoSaida(
    val status: StatusSaida,
    val registro: RegistroAcesso? = null
)
