package com.skillsmp.service

import com.skillsmp.domain.EntityStatus
import com.skillsmp.domain.Skill
import com.skillsmp.dto.*
import com.skillsmp.repository.*
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class SkillService(
    private val skillRepository: SkillRepository,
    private val projectRepository: ProjectRepository,
    private val userRepository: UserRepository,
    private val tagRepository: TagRepository,
    private val versionRepository: VersionRepository,
    private val variantRepository: VariantRepository,
    private val testRunRepository: TestRunRepository,
    private val auditService: AuditService
) {
    @Transactional
    fun create(projectId: Long, request: CreateSkillRequest, username: String): SkillDto {
        val project = projectRepository.findById(projectId)
            .orElseThrow { EntityNotFoundException("Project not found: $projectId") }
        val owner = userRepository.findByUsername(username)
            .orElseThrow { EntityNotFoundException("User not found") }

        val skill = Skill().apply {
            this.project = project
            name = request.name
            description = request.description
            promptBody = request.promptBody
            metadata = request.metadata ?: "{}"
            this.owner = owner
        }

        request.tagIds?.forEach { tagId ->
            val tag = tagRepository.findById(tagId)
                .orElseThrow { EntityNotFoundException("Tag not found: $tagId") }
            skill.tags.add(tag)
        }

        val saved = skillRepository.save(skill)
        auditService.log(owner.id, "CREATE", "SKILL", saved.id)
        return saved.toDto()
    }

    @Transactional
    fun update(id: Long, request: UpdateSkillRequest): SkillDto {
        val skill = skillRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Skill not found: $id") }

        request.name?.let { skill.name = it }
        request.description?.let { skill.description = it }
        request.promptBody?.let { skill.promptBody = it }
        request.metadata?.let { skill.metadata = it }
        request.status?.let { skill.status = EntityStatus.valueOf(it) }
        skill.updatedAt = LocalDateTime.now()

        return skillRepository.save(skill).toDto()
    }

    @Transactional
    fun delete(id: Long) {
        if (!skillRepository.existsById(id)) {
            throw EntityNotFoundException("Skill not found: $id")
        }
        skillRepository.deleteById(id)
    }

    fun getById(id: Long): SkillDetailDto {
        val skill = skillRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Skill not found: $id") }
        return skill.toDetailDto()
    }

    fun getByProjectId(projectId: Long): List<SkillDto> {
        return skillRepository.findByProjectId(projectId).map { it.toDto() }
    }

    @Transactional
    fun updateStatus(id: Long, status: String): SkillDto {
        val skill = skillRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Skill not found: $id") }
        skill.status = EntityStatus.valueOf(status)
        skill.updatedAt = LocalDateTime.now()
        return skillRepository.save(skill).toDto()
    }

    private fun Skill.toDto(): SkillDto = SkillDto(
        id = id,
        name = name,
        description = description,
        status = status.name,
        owner = owner.toDto(),
        tags = tags.map { it.toDto() },
        projectId = project.id,
        updatedAt = updatedAt,
        createdAt = createdAt
    )

    private fun Skill.toDetailDto(): SkillDetailDto {
        val versions = versionRepository.findBySkillIdOrderByCreatedAtDesc(id)
        val latestVersion = versions.firstOrNull()
        val variantCount = variantRepository.findBySkillId(id).size
        val testRunCount = versions.flatMap { v -> testRunRepository.findByVersionId(v.id) }.size

        return SkillDetailDto(
            id = id,
            name = name,
            description = description,
            status = status.name,
            owner = owner.toDto(),
            tags = tags.map { it.toDto() },
            projectId = project.id,
            updatedAt = updatedAt,
            createdAt = createdAt,
            promptBody = promptBody,
            metadata = metadata,
            latestVersion = latestVersion?.let {
                VersionDto(
                    id = it.id,
                    skillId = it.skill.id,
                    versionNumber = it.versionNumber,
                    content = it.content,
                    changelog = it.changelog,
                    status = it.status.name,
                    author = it.author.toDto(),
                    gitCommitSha = it.gitCommitSha,
                    createdAt = it.createdAt
                )
            },
            variantCount = variantCount,
            testRunCount = testRunCount
        )
    }
}
