package com.example.condominio.service

import com.example.condominio.model.Usuario
import com.example.condominio.repository.UsuarioRepository
import com.example.condominio.security.UserDetailImplementation
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UsuarioService(
    private val repository: UsuarioRepository,
    private val passwordEncoder: PasswordEncoder
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val usuario = repository.findByEmail(username)
            ?: throw UsernameNotFoundException("Usuário não encontrado: $username")
        return UserDetailImplementation(usuario)
    }

    fun cadastrar(usuario: Usuario): Boolean {
        if (repository.findByEmail(usuario.email) != null || repository.findByCpf(usuario.cpf) != null) {
            return false
        }
        val senhaCodificada = passwordEncoder.encode(usuario.senha) ?: return false
        repository.save(usuario.copy(senha = senhaCodificada))
        return true
    }

    fun lerTodos(): List<Usuario> {
        return repository.findAll()
    }

    fun lerPorId(id: Long): Usuario? {
        return repository.findById(id).orElse(null)
    }

    fun atualizar(id: Long, dados: Usuario): Boolean {
        val existente = repository.findById(id).orElse(null) ?: return false
        repository.save(
            existente.copy(
                nome = dados.nome,
                cpf = dados.cpf,
                telefone = dados.telefone,
                active = dados.active,
                foto = dados.foto
            )
        )
        return true
    }

    fun alterarPapel(id: Long, papel: String): Boolean {
        val existente = repository.findById(id).orElse(null) ?: return false
        repository.save(existente.copy(papeis = papel))
        return true
    }

    fun apagar(id: Long): Boolean {
        val usuario = repository.findByIdAndActiveTrue(id) ?: return false
        repository.save(usuario.copy(active = false))
        return true
    }
}