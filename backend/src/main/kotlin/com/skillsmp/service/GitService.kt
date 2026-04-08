package com.skillsmp.service

import com.skillsmp.domain.GitConfig
import com.skillsmp.dto.ConnectGitRequest
import com.skillsmp.dto.GitConfigDto
import com.skillsmp.dto.GitStatusDto
import com.skillsmp.dto.GitSyncResultDto
import com.skillsmp.repository.GitConfigRepository
import com.skillsmp.repository.ProjectRepository
import com.skillsmp.repository.VersionRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Service
class GitService(
    private val gitConfigRepository: GitConfigRepository,
    private val projectRepository: ProjectRepository,
    private val versionRepository: VersionRepository
) {
    @Transactional
    fun connect(projectId: Long, request: ConnectGitRequest): GitConfigDto {
        val project = projectRepository.findById(projectId)
            .orElseThrow { EntityNotFoundException("Project not found: $projectId") }

        val existing = gitConfigRepository.findByProjectId(projectId)
        val config = if (existing.isPresent) {
            existing.get().apply {
                repoUrl = request.repoUrl
                branch = request.branch
                token = request.token
                connected = true
            }
        } else {
            GitConfig().apply {
                this.project = project
                repoUrl = request.repoUrl
                branch = request.branch
                token = request.token
                connected = true
            }
        }

        return gitConfigRepository.save(config).toDto()
    }

    fun getStatus(projectId: Long): GitStatusDto {
        val config = gitConfigRepository.findByProjectId(projectId)
        return if (config.isPresent) {
            val c = config.get()
            GitStatusDto(
                connected = c.connected,
                lastSync = c.lastSyncAt,
                repoUrl = c.repoUrl,
                branch = c.branch
            )
        } else {
            GitStatusDto(connected = false, lastSync = null, repoUrl = null, branch = null)
        }
    }

    @Transactional
    fun sync(versionId: Long): GitSyncResultDto {
        val version = versionRepository.findById(versionId)
            .orElseThrow { EntityNotFoundException("Version not found: $versionId") }

        val config = gitConfigRepository.findByProjectId(version.skill.project.id)
        if (!config.isPresent || !config.get().connected) {
            return GitSyncResultDto(success = false, commitSha = null, message = "Git not connected for this project")
        }

        val commitSha = UUID.randomUUID().toString().take(8)
        version.gitCommitSha = commitSha
        versionRepository.save(version)

        val gitConfig = config.get()
        gitConfig.lastSyncAt = LocalDateTime.now()
        gitConfigRepository.save(gitConfig)

        return GitSyncResultDto(success = true, commitSha = commitSha, message = "Synced successfully (mock)")
    }

    private fun GitConfig.toDto() = GitConfigDto(
        id = id,
        projectId = project.id,
        repoUrl = repoUrl,
        branch = branch,
        connected = connected
    )
}
