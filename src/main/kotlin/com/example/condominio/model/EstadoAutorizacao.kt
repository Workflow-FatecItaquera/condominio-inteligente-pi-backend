package com.example.condominio.model

/**
 * Estados de uma autorização de acesso (diagrama de transição de estados do Quadro 2b):
 * AGENDADA -> VIGENTE -> EXPIRADA, e CANCELADA a partir de qualquer um deles (RN05).
 */
enum class EstadoAutorizacao {
    AGENDADA,
    VIGENTE,
    EXPIRADA,
    CANCELADA
}
