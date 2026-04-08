package com.skillsmp.controller

import com.skillsmp.dto.*
import com.skillsmp.service.SkillService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.security.Principal

@RestController
class SkillController(
    private val skillService: SkillService
) {
    @GetMapping("/api/projects/{projectId}/skills")
    fun getByProjectId(@PathVariable projectId: Long): ResponseEntity<List<SkillDto>> {
        return ResponseEntity.ok(skillService.getByProjectId(projectId))
    }

    @PostMapping("/api/projects/{projectId}/skills")
    fun create(
        @PathVariable projectId: Long,
        @RequestBody request: CreateSkillRequest,
        principal: Principal
    ): ResponseEntity<SkillDto> {
        return ResponseEntity.ok(skillService.create(projectId, request, principal.name))
    }

    @GetMapping("/api/skills/{id}")
    fun getById(@PathVariable id: Long): ResponseEntity<SkillDetailDto> {
        return ResponseEntity.ok(skillService.getById(id))
    }

    @PutMapping("/api/skills/{id}")
    fun update(@PathVariable id: Long, @RequestBody request: UpdateSkillRequest): ResponseEntity<SkillDto> {
        return ResponseEntity.ok(skillService.update(id, request))
    }

    @DeleteMapping("/api/skills/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        skillService.delete(id)
        return ResponseEntity.noContent().build()
    }

    @PatchMapping("/api/skills/{id}/status")
    fun updateStatus(
        @PathVariable id: Long,
        @RequestBody request: StatusUpdateRequest
    ): ResponseEntity<SkillDto> {
        return ResponseEntity.ok(skillService.updateStatus(id, request.status))
    }
}
