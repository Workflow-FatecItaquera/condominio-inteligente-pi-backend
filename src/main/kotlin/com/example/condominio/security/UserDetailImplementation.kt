package com.example.condominio.security

import com.example.condominio.model.Usuario
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class UserDetailImplementation(
    val usuario: Usuario
) : UserDetails {

    override fun getPassword(): String {
        return usuario.senha
    }

    override fun getUsername(): String {
        return usuario.email
    }

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return usuario.papeis.split(",").map { papel ->
            SimpleGrantedAuthority("ROLE_${papel.trim()}")
        }
    }

    override fun isEnabled(): Boolean { return true }
    override fun isCredentialsNonExpired(): Boolean { return true }
    override fun isAccountNonExpired(): Boolean { return true }
    override fun isAccountNonLocked(): Boolean { return true }
}