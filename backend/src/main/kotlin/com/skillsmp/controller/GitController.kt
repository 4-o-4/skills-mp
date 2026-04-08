package com.skillsmp.controller

import com.skillsmp.dto.ConnectGitRequest
import com.skillsmp.dto.GitConfigDto
import com.skillsmp.dto.GitStatusDto
import com.skillsmp.dto.GitSyncResultDto
import com.skillsmp.service.GitService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/git")
class GitController(
    private val gitService: GitService
) {
    @PostMapping("/connect/{projectId}")
    fun connect(
        @PathVariable projectId: Long,
        @RequestBody request: ConnectGitRequest
    ): ResponseEntity<GitConfigDto> {
        return ResponseEntity.ok(gitService.connect(projectId, request))
    }

    @GetMapping("/status/{projectId}")
    fun getStatus(@PathVariable projectId: Long): ResponseEntity<GitStatusDto> {
        return ResponseEntity.ok(gitService.getStatus(projectId))
    }

    @PostMapping("/sync/{versionId}")
    fun sync(@PathVariable versionId: Long): ResponseEntity<GitSyncResultDto> {
        return ResponseEntity.ok(gitService.sync(versionId))
    }
}
