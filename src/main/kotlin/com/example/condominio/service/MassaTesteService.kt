package com.example.condominio.service

import com.example.condominio.model.Apartamento
import com.example.condominio.model.AutorizacaoAcesso
import com.example.condominio.model.Condominio
import com.example.condominio.model.ListaAutorizados
import com.example.condominio.model.Usuario
import com.example.condominio.model.Visitante
import com.example.condominio.repository.ApartamentoRepository
import com.example.condominio.repository.AutorizacaoAcessoRepository
import com.example.condominio.repository.CondominioRepository
import com.example.condominio.repository.FeriadoRepository
import com.example.condominio.repository.ListaAutorizadosRepository
import com.example.condominio.repository.RegistroAcessoRepository
import com.example.condominio.repository.UsuarioRepository
import com.example.condominio.repository.VisitanteRepository
import com.example.condominio.security.Papeis
import org.springframework.context.annotation.Profile
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalTime

/**
 * Massa-base B0 dos testes de sistema (somente perfil "teste", banco H2 em memória).
 * Tudo é sintético e relativo ao dia D do relógio do sistema.
 */
@Service
@Profile("teste")
class MassaTesteService(
    private val relogio: RelogioControlavel,
    private val condominioRepository: CondominioRepository,
    private val apartamentoRepository: ApartamentoRepository,
    private val usuarioRepository: UsuarioRepository,
    private val visitanteRepository: VisitanteRepository,
    private val listaRepository: ListaAutorizadosRepository,
    private val autorizacaoRepository: AutorizacaoAcessoRepository,
    private val registroRepository: RegistroAcessoRepository,
    private val feriadoRepository: FeriadoRepository,
    private val passwordEncoder: PasswordEncoder
) {

    companion object {
        const val SENHA = "Teste@123"
        const val EMAIL_RESPONSAVEL = "responsavel.r01@teste.local"
        const val EMAIL_PORTARIA = "portaria.p01@teste.local"
        const val DOCUMENTO_V01 = "DOC-V01"
        const val DOCUMENTO_V02 = "DOC-V02"
        private const val CNPJ_TESTE = "99999999000199"
    }

    /**
     * Restaura B0 e devolve os ids criados. D é o dia do relógio de teste (na planilha, 11/11/2026).
     * revisaoLista é opcional: sem ela a lista das unidades foi revisada em D menos 6 meses
     * (vale até D, inclusive); com ela a lista fica revisada na data informada (CT04 e CT18).
     */
    fun restaurarB0(revisaoLista: LocalDate? = null): Map<String, Any> {
        val dia = relogio.agora().toLocalDate()

        registroRepository.deleteAll()
        autorizacaoRepository.deleteAll()
        listaRepository.deleteAll()
        feriadoRepository.deleteAll()

        val condominio = condominioRepository.findAll().firstOrNull { it.cnpj == CNPJ_TESTE }
            ?: condominioRepository.save(
                Condominio(nome = "Condomínio C01 (teste)", rua = "Rua de Teste", numero = 1,
                    bairro = "Centro", cidade = "São Paulo", cnpj = CNPJ_TESTE)
            )
        val u101 = obterUnidade(condominio, 101)
        val u102 = obterUnidade(condominio, 102)

        val r01 = obterUsuario(EMAIL_RESPONSAVEL, "Responsavel R01", "99999999901", Papeis.MORADOR)
        val p01 = obterUsuario(EMAIL_PORTARIA, "Portaria P01", "99999999902", Papeis.PORTARIA)

        val v01 = obterVisitante(DOCUMENTO_V01, "Visitante V01")
        val v02 = obterVisitante(DOCUMENTO_V02, "Visitante V02")

        // Listas das duas unidades com V01 e V02, revisadas em D menos 6 meses (último dia de validade = D)
        val revisao = revisaoLista ?: dia.minusMonths(6)
        listaRepository.save(ListaAutorizados(unidade = u101, responsavel = r01, atualizadaEm = revisao, visitantes = mutableSetOf(v01, v02)))
        listaRepository.save(ListaAutorizados(unidade = u102, responsavel = r01, atualizadaEm = revisao, visitantes = mutableSetOf(v01, v02)))

        // A01: V01 e U101, de D a D+19 (11/11 a 30/11/2026), 08:00 às 18:00, sem fim de semana nem feriado
        val a01 = autorizacaoRepository.save(
            AutorizacaoAcesso(
                visitante = v01,
                unidade = u101,
                responsavel = r01,
                dataInicio = dia,
                dataFim = dia.plusDays(19),
                horaInicio = LocalTime.of(8, 0),
                horaFim = LocalTime.of(18, 0)
            )
        )

        return mapOf(
            "diaD" to dia.toString(),
            "unidadeU101" to (u101.id ?: 0L),
            "unidadeU102" to (u102.id ?: 0L),
            "visitanteV01" to (v01.id ?: 0L),
            "visitanteV02" to (v02.id ?: 0L),
            "autorizacaoA01" to (a01.id ?: 0L),
            "responsavelR01" to EMAIL_RESPONSAVEL,
            "portariaP01" to EMAIL_PORTARIA,
            "senha" to SENHA
        )
    }

    private fun obterUnidade(condominio: Condominio, numero: Int): Apartamento {
        val existente = apartamentoRepository.findAll()
            .firstOrNull { it.condominio.id == condominio.id && it.numero == numero }
        if (existente != null) {
            return existente
        }
        return apartamentoRepository.save(
            Apartamento(condominio = condominio, bloco = "A", numero = numero, andar = "1", interfone = numero.toString())
        )
    }

    private fun obterUsuario(email: String, nome: String, cpf: String, papel: String): Usuario {
        val existente = usuarioRepository.findByEmail(email)
        if (existente != null) {
            return existente
        }
        return usuarioRepository.save(
            Usuario(nome = nome, cpf = cpf, email = email, senha = passwordEncoder.encode(SENHA)!!, papeis = papel)
        )
    }

    private fun obterVisitante(documento: String, nome: String): Visitante {
        val existente = visitanteRepository.findFirstByDocumento(documento)
        if (existente != null) {
            return visitanteRepository.save(existente.copy(active = true, nome = nome))
        }
        return visitanteRepository.save(Visitante(nome = nome, documento = documento))
    }
}
