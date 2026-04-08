package com.skillsmp.controller

import com.skillsmp.dto.CreateReleaseRequest
import com.skillsmp.dto.ReleaseDto
import com.skillsmp.service.ReleaseService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.security.Principal

@RestController
class ReleaseController(
    private val releaseService: ReleaseService
) {
    @GetMapping("/api/projects/{projectId}/releases")
    fun getByProjectId(@PathVariable projectId: Long): ResponseEntity<List<ReleaseDto>> {
        return ResponseEntity.ok(releaseService.getByProjectId(projectId))
    }

    @PostMapping("/api/projects/{projectId}/releases")
    fun create(
        @PathVariable projectId: Long,
        @RequestBody request: CreateReleaseRequest,
        principal: Principal
    ): ResponseEntity<ReleaseDto> {
        return ResponseEntity.ok(releaseService.create(projectId, request, principal.name))
    }

    @GetMapping("/api/releases/{id}")
    fun getById(@PathVariable id: Long): ResponseEntity<ReleaseDto> {
        return ResponseEntity.ok(releaseService.getById(id))
    }
}
