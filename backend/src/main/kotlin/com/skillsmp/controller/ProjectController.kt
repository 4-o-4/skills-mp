package com.skillsmp.controller

import com.skillsmp.dto.CreateProjectRequest
import com.skillsmp.dto.ProjectDto
import com.skillsmp.service.ProjectService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.security.Principal

@RestController
@RequestMapping("/api/projects")
class ProjectController(
    private val projectService: ProjectService
) {
    @GetMapping
    fun getAll(@RequestParam(required = false) ownerId: Long?): ResponseEntity<List<ProjectDto>> {
        return ResponseEntity.ok(projectService.getAll(ownerId))
    }

    @GetMapping("/my")
    fun getMy(principal: Principal): ResponseEntity<List<ProjectDto>> {
        return ResponseEntity.ok(projectService.getByOwnerUsername(principal.name))
    }

    @PostMapping
    fun create(@RequestBody request: CreateProjectRequest, principal: Principal): ResponseEntity<ProjectDto> {
        return ResponseEntity.ok(projectService.create(request, principal.name))
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ResponseEntity<ProjectDto> {
        return ResponseEntity.ok(projectService.getById(id))
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody request: CreateProjectRequest): ResponseEntity<ProjectDto> {
        return ResponseEntity.ok(projectService.update(id, request))
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        projectService.delete(id)
        return ResponseEntity.noContent().build()
    }
}
