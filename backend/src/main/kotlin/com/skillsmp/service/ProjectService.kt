package com.skillsmp.service

import com.skillsmp.domain.Project
import com.skillsmp.dto.CreateProjectRequest
import com.skillsmp.dto.ProjectDto
import com.skillsmp.repository.ProjectRepository
import com.skillsmp.repository.SkillRepository
import com.skillsmp.repository.UserRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class ProjectService(
    private val projectRepository: ProjectRepository,
    private val userRepository: UserRepository,
    private val skillRepository: SkillRepository,
    private val auditService: AuditService
) {
    @Transactional
    fun create(request: CreateProjectRequest, username: String): ProjectDto {
        val owner = userRepository.findByUsername(username)
            .orElseThrow { EntityNotFoundException("User not found") }

        val project = Project().apply {
            name = request.name
            description = request.description
            this.owner = owner
        }
        val saved = projectRepository.save(project)
        auditService.log(owner.id, "CREATE", "PROJECT", saved.id)
        return saved.toDto()
    }

    @Transactional
    fun update(id: Long, request: CreateProjectRequest): ProjectDto {
        val project = projectRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Project not found: $id") }

        project.name = request.name
        project.description = request.description
        project.updatedAt = LocalDateTime.now()

        return projectRepository.save(project).toDto()
    }

    @Transactional
    fun delete(id: Long) {
        if (!projectRepository.existsById(id)) {
            throw EntityNotFoundException("Project not found: $id")
        }
        projectRepository.deleteById(id)
    }

    fun getById(id: Long): ProjectDto {
        val project = projectRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Project not found: $id") }
        return project.toDto()
    }

    fun getAll(ownerId: Long?): List<ProjectDto> {
        val projects = if (ownerId != null) {
            projectRepository.findByOwnerId(ownerId)
        } else {
            projectRepository.findAll()
        }
        return projects.map { it.toDto() }
    }

    fun getByOwnerUsername(username: String): List<ProjectDto> {
        val owner = userRepository.findByUsername(username)
            .orElseThrow { EntityNotFoundException("User not found") }
        return projectRepository.findByOwnerId(owner.id).map { it.toDto() }
    }

    private fun Project.toDto(): ProjectDto {
        val skillCount = skillRepository.findByProjectId(id).size
        return ProjectDto(
            id = id,
            name = name,
            description = description,
            owner = owner.toDto(),
            status = status,
            skillCount = skillCount,
            updatedAt = updatedAt,
            createdAt = createdAt
        )
    }
}
