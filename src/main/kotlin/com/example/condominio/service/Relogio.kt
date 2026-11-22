package com.example.condominio.service

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.LocalDateTime

/** Dependência controlável: permite trocar o relógio nos testes (driver/stub). */
interface Relogio {
    fun agora(): LocalDateTime
}

@Component
@Profile("!teste")
class RelogioSistema : Relogio {
    override fun agora(): LocalDateTime {
        return LocalDateTime.now()
    }
}

/**
 * Relógio ajustável, ativo somente no perfil "teste". Por padrão guarda um deslocamento em
 * relação ao relógio real, então o tempo continua correndo depois do ajuste. Com
 * congelar = true o relógio fica parado no instante definido, o que permite testar limites
 * exatos de horário (por exemplo 18:00:00 e 18:00:01) e deixa as evidências determinísticas.
 * Serve aos testes de sistema com estados por tempo (T1, T4) e limites de horário.
 */
@Component
@Profile("teste")
class RelogioControlavel : Relogio {

    @Volatile
    private var deslocamento: Duration = Duration.ZERO

    @Volatile
    private var congeladoEm: LocalDateTime? = null

    override fun agora(): LocalDateTime {
        return congeladoEm ?: LocalDateTime.now().plus(deslocamento)
    }

    fun definir(alvo: LocalDateTime, congelar: Boolean = false) {
        if (congelar) {
            congeladoEm = alvo
        } else {
            congeladoEm = null
            deslocamento = Duration.between(LocalDateTime.now(), alvo)
        }
    }

    fun restaurar() {
        congeladoEm = null
        deslocamento = Duration.ZERO
    }
}
