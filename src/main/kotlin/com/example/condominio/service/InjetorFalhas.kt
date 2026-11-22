package com.example.condominio.service

import org.springframework.stereotype.Component

/** Pontos do fluxo em que uma falha técnica pode ser induzida (caminhos C3, C10 e C11). */
enum class PontoDeFalha {
    CONSULTA,
    GRAVAR_ENTRADA,
    GRAVAR_SAIDA
}

/** Permite induzir falhas técnicas para testar os fluxos de exceção. Nos testes de unidade é um mock. */
interface InjetorFalhas {
    fun verificar(ponto: PontoDeFalha)
}

/** Implementação padrão: nunca induz falha. */
@Component
class InjetorFalhasInativo : InjetorFalhas {

    override fun verificar(ponto: PontoDeFalha) {
        // nenhuma falha induzida
    }
}
