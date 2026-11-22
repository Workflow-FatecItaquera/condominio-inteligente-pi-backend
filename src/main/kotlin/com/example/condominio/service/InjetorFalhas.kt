package com.example.condominio.service

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

enum class PontoDeFalha {
    CONSULTA,
    GRAVAR_ENTRADA,
    GRAVAR_SAIDA
}

/**
 * Dependência controlável que permite provocar falhas nos testes de sistema
 * (CT15 grava entrada, CT16 grava saída e CT17 consulta) sem mexer no banco. Em produção a implementação não faz nada.
 */
interface InjetorFalhas {
    fun verificar(ponto: PontoDeFalha)
}

@Component
@Profile("!teste")
class SemFalhas : InjetorFalhas {
    override fun verificar(ponto: PontoDeFalha) {
        // produção: nenhuma falha induzida
    }
}

/** Ativo somente no perfil "teste"; as falhas são ligadas e desligadas por /admin/teste/falhas. */
@Component
@Profile("teste")
class FalhasControlaveis : InjetorFalhas {

    private val ativas: MutableSet<PontoDeFalha> = ConcurrentHashMap.newKeySet()

    override fun verificar(ponto: PontoDeFalha) {
        if (ativas.contains(ponto)) {
            throw IllegalStateException("Falha induzida pelo ambiente de teste em $ponto")
        }
    }

    fun ativar(ponto: PontoDeFalha) {
        ativas.add(ponto)
    }

    fun limpar() {
        ativas.clear()
    }

    fun ativas(): Set<PontoDeFalha> {
        return ativas.toSet()
    }
}
