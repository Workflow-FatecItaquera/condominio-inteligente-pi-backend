package com.example.condominio.security

import com.example.condominio.model.Usuario
import com.example.condominio.service.UsuarioService
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class AdminInicial(
    private val service: UsuarioService,
    @Value("\${app.admin.nome:Administrador}") private val nome: String,
    @Value("\${app.admin.cpf:00000000000}") private val cpf: String,
    @Value("\${app.admin.telefone:99999999}") private val telefone: String,
    @Value("\${app.admin.email:admin@condominio.com}") private val email: String,
    @Value("\${app.admin.senha:admin123}") private val senha: String
) : CommandLineRunner {

    override fun run(vararg args: String) {
        val admin = Usuario(
            nome = nome,
            cpf = cpf,
            telefone = telefone,
            email = email,
            senha = senha,
            papeis = Papeis.ADMIN,
            active = true,
            dataRegistro = LocalDateTime.now()
        )
        if (service.cadastrar(admin)) {
            println("Usuário ADMIN inicial criado: $email")
        }
    }
}