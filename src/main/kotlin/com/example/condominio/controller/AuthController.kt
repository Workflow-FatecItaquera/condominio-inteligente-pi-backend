package com.example.condominio.controller

import com.example.condominio.dto.CredenciaisDTO
import com.example.condominio.dto.UsuarioCadastroDTO
import com.example.condominio.model.Usuario
import com.example.condominio.security.JwtUtils
import com.example.condominio.security.Papeis
import com.example.condominio.security.UserDetailImplementation
import com.example.condominio.service.UsuarioService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(
    private val service: UsuarioService,
    private val jwt: JwtUtils,
    private val authenticationManager: AuthenticationManager
) {

    @PostMapping("/signin")
    fun login(@RequestBody credenciaisDTO: CredenciaisDTO): ResponseEntity<Any> {
        return try {
            val authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(
                    credenciaisDTO.email,
                    credenciaisDTO.senha
                )
            )
            val userDetails = authentication.principal as UserDetailImplementation
            val claims = mapOf("roles" to userDetails.authorities.map { it.authority }, "email" to userDetails.username)
            val token = jwt.generateToken(userDetails.username, claims)
            ResponseEntity.ok(token)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciais inválidas")
        }
    }

    @PostMapping("/signup")
    fun register(@Valid @RequestBody usuarioCadastroDTO: UsuarioCadastroDTO): ResponseEntity<String> {
        val usuario = Usuario(
            nome = usuarioCadastroDTO.nome,
            cpf = usuarioCadastroDTO.cpf,
            email = usuarioCadastroDTO.email,
            senha = usuarioCadastroDTO.senha,
            telefone = usuarioCadastroDTO.telefone,
            papeis = Papeis.MORADOR,
            active = true
        )
        return if (service.cadastrar(usuario)) {
            ResponseEntity.status(HttpStatus.CREATED).body("Usuário cadastrado com sucesso")
        } else {
            ResponseEntity.status(HttpStatus.CONFLICT).body("E-mail ou CPF já cadastrado")
        }
    }
}