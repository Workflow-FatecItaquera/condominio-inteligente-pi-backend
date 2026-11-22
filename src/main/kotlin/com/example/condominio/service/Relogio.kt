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
 * Relógio ajustável, ativo somente no perfil "teste". Guarda um deslocamento em
 * relação ao relógio real, então o tempo continua correndo depois do ajuste.
 * Serve aos testes de sistema com estados por tempo (T1, T4) e limites de horário.
 */
@Component
@Profile("teste")
class RelogioControlavel : Relogio {

    @Volatile
    private var deslocamento: Duration = Duration.ZERO

    override fun agora(): LocalDateTime {
        return LocalDateTime.now().plus(deslocamento)
    }

    fun definir(alvo: LocalDateTime) {
        deslocamento = Duration.between(LocalDateTime.now(), alvo)
    }

    fun restaurar() {
        deslocamento = Duration.ZERO
    }
}
