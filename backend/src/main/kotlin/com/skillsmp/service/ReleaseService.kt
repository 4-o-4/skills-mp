package com.skillsmp.service

import com.skillsmp.domain.Release
import com.skillsmp.dto.CreateReleaseRequest
import com.skillsmp.dto.ReleaseDto
import com.skillsmp.repository.*
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ReleaseService(
    private val releaseRepository: ReleaseRepository,
    private val projectRepository: ProjectRepository,
    private val versionRepository: VersionRepository,
    private val environmentRepository: EnvironmentRepository,
    private val userRepository: UserRepository
) {
    @Transactional
    fun create(projectId: Long, request: CreateReleaseRequest, username: String): ReleaseDto {
        val project = projectRepository.findById(projectId)
            .orElseThrow { EntityNotFoundException("Project not found: $projectId") }
        val version = versionRepository.findById(request.versionId)
            .orElseThrow { EntityNotFoundException("Version not found: ${request.versionId}") }
        val environment = environmentRepository.findById(request.environmentId)
            .orElseThrow { EntityNotFoundException("Environment not found: ${request.environmentId}") }
        val user = userRepository.findByUsername(username)
            .orElseThrow { EntityNotFoundException("User not found") }

        environment.activeVersion = version
        environmentRepository.save(environment)

        val release = Release().apply {
            this.project = project
            this.version = version
            this.environment = environment
            gitTag = request.gitTag
            notes = request.notes
            createdBy = user
        }

        return releaseRepository.save(release).toDto()
    }

    fun getByProjectId(projectId: Long): List<ReleaseDto> {
        return releaseRepository.findByProjectId(projectId).map { it.toDto() }
    }

    fun getById(id: Long): ReleaseDto {
        return releaseRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Release not found: $id") }
            .toDto()
    }

    private fun Release.toDto() = ReleaseDto(
        id = id,
        projectId = project.id,
        versionId = version.id,
        versionNumber = version.versionNumber,
        environmentId = environment.id,
        environmentName = environment.name,
        gitTag = gitTag,
        notes = notes,
        createdBy = createdBy.toDto(),
        createdAt = createdAt
    )
}
