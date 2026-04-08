package com.skillsmp.controller

import com.skillsmp.dto.CreateVersionRequest
import com.skillsmp.dto.DiffDto
import com.skillsmp.dto.VersionDto
import com.skillsmp.service.VersionService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.security.Principal

@RestController
class VersionController(
    private val versionService: VersionService
) {
    @GetMapping("/api/skills/{skillId}/versions")
    fun getBySkillId(@PathVariable skillId: Long): ResponseEntity<List<VersionDto>> {
        return ResponseEntity.ok(versionService.getBySkillId(skillId))
    }

    @PostMapping("/api/skills/{skillId}/versions")
    fun create(
        @PathVariable skillId: Long,
        @RequestBody request: CreateVersionRequest,
        principal: Principal
    ): ResponseEntity<VersionDto> {
        return ResponseEntity.ok(versionService.create(skillId, request, principal.name))
    }

    @GetMapping("/api/versions/{id}")
    fun getById(@PathVariable id: Long): ResponseEntity<VersionDto> {
        return ResponseEntity.ok(versionService.getById(id))
    }

    @GetMapping("/api/versions/{id1}/diff/{id2}")
    fun getDiff(@PathVariable id1: Long, @PathVariable id2: Long): ResponseEntity<DiffDto> {
        return ResponseEntity.ok(versionService.getDiff(id1, id2))
    }

    @PostMapping("/api/versions/{id}/rollback")
    fun rollback(@PathVariable id: Long, principal: Principal): ResponseEntity<VersionDto> {
        return ResponseEntity.ok(versionService.rollback(id, principal.name))
    }
}
