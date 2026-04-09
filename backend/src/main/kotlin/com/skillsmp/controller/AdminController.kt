package com.skillsmp.controller

import com.skillsmp.dto.UserDto
import com.skillsmp.service.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
class AdminController(
    private val authService: AuthService
) {
    @GetMapping("/users")
    fun getAllUsers(): ResponseEntity<List<UserDto>> {
        return ResponseEntity.ok(authService.getAllUsers())
    }
}
