package com.skillsmp.service

import com.skillsmp.domain.Environment
import com.skillsmp.domain.EnvironmentDeployment
import com.skillsmp.dto.*
import com.skillsmp.repository.*
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class EnvironmentService(
    private val environmentRepository: EnvironmentRepository,
    private val environmentDeploymentRepository: EnvironmentDeploymentRepository,
    private val projectRepository: ProjectRepository,
    private val versionRepository: VersionRepository,
    private val userRepository: UserRepository
) {
    @Transactional
    fun create(request: CreateEnvironmentRequest): EnvironmentDto {
        val project = projectRepository.findById(request.projectId)
            .orElseThrow { EntityNotFoundException("Project not found: ${request.projectId}") }

        val env = Environment().apply {
            name = request.name
            description = request.description
            this.project = project
        }
        return environmentRepository.save(env).toDto()
    }

    fun getByProjectId(projectId: Long): List<EnvironmentDto> {
        return environmentRepository.findByProjectId(projectId).map { it.toDto() }
    }

    fun getById(id: Long): EnvironmentDto {
        return environmentRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Environment not found: $id") }
            .toDto()
    }

    @Transactional
    fun deploy(envId: Long, versionId: Long, username: String): DeploymentDto {
        val env = environmentRepository.findById(envId)
            .orElseThrow { EntityNotFoundException("Environment not found: $envId") }
        val version = versionRepository.findById(versionId)
            .orElseThrow { EntityNotFoundException("Version not found: $versionId") }
        val user = userRepository.findByUsername(username)
            .orElseThrow { EntityNotFoundException("User not found") }

        env.activeVersion = version
        environmentRepository.save(env)

        val deployment = EnvironmentDeployment().apply {
            environment = env
            this.version = version
            deployedBy = user
        }
        val saved = environmentDeploymentRepository.save(deployment)

        return DeploymentDto(
            id = saved.id,
            environmentId = env.id,
            versionId = version.id,
            versionNumber = version.versionNumber,
            deployedBy = user.toDto(),
            deployedAt = saved.deployedAt
        )
    }

    fun getDeployments(envId: Long): List<DeploymentDto> {
        return environmentDeploymentRepository.findByEnvironmentIdOrderByDeployedAtDesc(envId).map { d ->
            DeploymentDto(
                id = d.id,
                environmentId = d.environment.id,
                versionId = d.version.id,
                versionNumber = d.version.versionNumber,
                deployedBy = d.deployedBy.toDto(),
                deployedAt = d.deployedAt
            )
        }
    }

    private fun Environment.toDto() = EnvironmentDto(
        id = id,
        name = name,
        description = description,
        projectId = project.id,
        activeVersionId = activeVersion?.id,
        activeVersionNumber = activeVersion?.versionNumber,
        createdAt = createdAt
    )
}
