package com.skillsmp.controller

import com.skillsmp.dto.AuthResponse
import com.skillsmp.dto.LoginRequest
import com.skillsmp.dto.RegisterRequest
import com.skillsmp.dto.UserDto
import com.skillsmp.service.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.security.Principal

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService
) {
    @PostMapping("/register")
    fun register(@RequestBody request: RegisterRequest): ResponseEntity<AuthResponse> {
        return ResponseEntity.ok(authService.register(request))
    }

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<AuthResponse> {
        return ResponseEntity.ok(authService.login(request))
    }

    @GetMapping("/me")
    fun me(principal: Principal): ResponseEntity<UserDto> {
        return ResponseEntity.ok(authService.getCurrentUser(principal.name))
    }
}
