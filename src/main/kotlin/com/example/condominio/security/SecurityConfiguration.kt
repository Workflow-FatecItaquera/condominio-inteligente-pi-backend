package com.example.condominio.security

import com.example.condominio.service.UsuarioService
import org.springframework.beans.factory.ObjectProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
class SecurityConfiguration(
    private val jwtUtils: JwtUtils,
    private val usuarioServiceProvider: ObjectProvider<UsuarioService>
) {

    @Bean
    fun jwtAuthenticationFilter(): JwtAuthenticationFilter {
        return JwtAuthenticationFilter(jwtUtils, usuarioServiceProvider)
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun authenticationManager(
        configuration: AuthenticationConfiguration
    ): AuthenticationManager {
        return configuration.authenticationManager
    }

    @Bean
    fun filterChain(http: HttpSecurity, jwtAuthenticationFilter: JwtAuthenticationFilter): SecurityFilterChain {
        http {
            csrf { disable() }
            formLogin { disable() }
            sessionManagement { sessionCreationPolicy = SessionCreationPolicy.STATELESS }
            authorizeHttpRequests {
                authorize("/auth/**", permitAll)
                authorize("/public/**", permitAll)
                authorize("/admin/**", hasRole("ADMIN"))

                // usuários
                authorize(HttpMethod.PUT, "/api/usuarios/*/papeis", hasRole("ADMIN"))
                authorize(HttpMethod.GET, "/api/usuarios/**", hasAnyRole("PORTARIA", "SINDICO", "ADMIN"))
                authorize(HttpMethod.PUT, "/api/usuarios/**", hasAnyRole("SINDICO", "ADMIN"))
                authorize(HttpMethod.DELETE, "/api/usuarios/**", hasRole("ADMIN"))

                // condomínios
                authorize(HttpMethod.GET, "/api/condominios/**", authenticated)
                authorize(HttpMethod.PUT, "/api/condominios/**", hasAnyRole("SINDICO", "ADMIN"))
                authorize("/api/condominios/**", hasRole("ADMIN"))

                // cobranças, pagamentos e despesas
                authorize(HttpMethod.GET, "/api/cobrancas/**", hasAnyRole("MORADOR", "SINDICO", "ADMIN"))
                authorize("/api/cobrancas/**", hasAnyRole("SINDICO", "ADMIN"))
                authorize("/api/pagamentos/**", hasAnyRole("SINDICO", "ADMIN"))
                authorize("/api/despesas/**", hasAnyRole("SINDICO", "ADMIN"))

                // apartamentos
                authorize(HttpMethod.GET, "/api/apartamentos/**", authenticated)
                authorize("/api/apartamentos/**", hasAnyRole("SINDICO", "ADMIN"))

                authorize(anyRequest, authenticated)
            }
            addFilterBefore<UsernamePasswordAuthenticationFilter>(jwtAuthenticationFilter)
        }
        return http.build()
    }
}