package com.skillsmp.controller

import com.skillsmp.dto.*
import com.skillsmp.service.EnvironmentService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.security.Principal

@RestController
@RequestMapping("/api/environments")
class EnvironmentController(
    private val environmentService: EnvironmentService
) {
    @GetMapping
    fun getByProjectId(@RequestParam projectId: Long): ResponseEntity<List<EnvironmentDto>> {
        return ResponseEntity.ok(environmentService.getByProjectId(projectId))
    }

    @PostMapping
    fun create(@RequestBody request: CreateEnvironmentRequest): ResponseEntity<EnvironmentDto> {
        return ResponseEntity.ok(environmentService.create(request))
    }

    @GetMapping("/{id}/deployments")
    fun getDeployments(@PathVariable id: Long): ResponseEntity<List<DeploymentDto>> {
        return ResponseEntity.ok(environmentService.getDeployments(id))
    }

    @PostMapping("/{id}/deploy")
    fun deploy(
        @PathVariable id: Long,
        @RequestBody request: DeployRequest,
        principal: Principal
    ): ResponseEntity<DeploymentDto> {
        return ResponseEntity.ok(environmentService.deploy(id, request.versionId, principal.name))
    }
}
