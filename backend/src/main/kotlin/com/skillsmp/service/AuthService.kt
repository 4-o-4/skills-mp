package com.skillsmp.service

import com.skillsmp.config.JwtUtil
import com.skillsmp.domain.User
import com.skillsmp.domain.UserRole
import com.skillsmp.dto.AuthResponse
import com.skillsmp.dto.LoginRequest
import com.skillsmp.dto.RegisterRequest
import com.skillsmp.dto.UserDto
import com.skillsmp.repository.UserRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtUtil: JwtUtil
) {
    @Transactional
    fun register(request: RegisterRequest): AuthResponse {
        if (userRepository.existsByUsername(request.username)) {
            throw IllegalArgumentException("Username already exists")
        }
        if (userRepository.existsByEmail(request.email)) {
            throw IllegalArgumentException("Email already exists")
        }

        val user = User().apply {
            username = request.username
            email = request.email
            passwordHash = passwordEncoder.encode(request.password)
            role = UserRole.EDITOR
        }
        val saved = userRepository.save(user)
        val token = jwtUtil.generateToken(saved.username)
        return AuthResponse(token, saved.toDto())
    }

    fun login(request: LoginRequest): AuthResponse {
        val user = userRepository.findByUsername(request.username)
            .orElseThrow { BadCredentialsException("Invalid credentials") }

        if (!passwordEncoder.matches(request.password, user.passwordHash)) {
            throw BadCredentialsException("Invalid credentials")
        }

        val token = jwtUtil.generateToken(user.username)
        return AuthResponse(token, user.toDto())
    }

    fun getCurrentUser(username: String): UserDto {
        val user = userRepository.findByUsername(username)
            .orElseThrow { EntityNotFoundException("User not found") }
        return user.toDto()
    }

    fun getAllUsers(): List<UserDto> {
        return userRepository.findAll().map { it.toDto() }
    }
}

fun User.toDto() = UserDto(
    id = id,
    username = username,
    email = email,
    role = role.name,
    createdAt = createdAt
)
