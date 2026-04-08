package com.skillsmp.service

import com.skillsmp.domain.Version
import com.skillsmp.dto.CreateVersionRequest
import com.skillsmp.dto.DiffDto
import com.skillsmp.dto.VersionDto
import com.skillsmp.repository.SkillRepository
import com.skillsmp.repository.UserRepository
import com.skillsmp.repository.VersionRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class VersionService(
    private val versionRepository: VersionRepository,
    private val skillRepository: SkillRepository,
    private val userRepository: UserRepository,
    private val auditService: AuditService
) {
    @Transactional
    fun create(skillId: Long, request: CreateVersionRequest, username: String): VersionDto {
        val skill = skillRepository.findById(skillId)
            .orElseThrow { EntityNotFoundException("Skill not found: $skillId") }
        val author = userRepository.findByUsername(username)
            .orElseThrow { EntityNotFoundException("User not found") }

        val existing = versionRepository.findBySkillIdOrderByCreatedAtDesc(skillId)
        val nextNumber = "v${existing.size + 1}"

        val version = Version().apply {
            this.skill = skill
            versionNumber = nextNumber
            content = request.content
            changelog = request.changelog
            this.author = author
        }

        val saved = versionRepository.save(version)
        auditService.log(author.id, "CREATE", "VERSION", saved.id)
        return saved.toDto()
    }

    fun getById(id: Long): VersionDto {
        val version = versionRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Version not found: $id") }
        return version.toDto()
    }

    fun getBySkillId(skillId: Long): List<VersionDto> {
        return versionRepository.findBySkillIdOrderByCreatedAtDesc(skillId).map { it.toDto() }
    }

    fun getDiff(id1: Long, id2: Long): DiffDto {
        val v1 = versionRepository.findById(id1)
            .orElseThrow { EntityNotFoundException("Version not found: $id1") }
        val v2 = versionRepository.findById(id2)
            .orElseThrow { EntityNotFoundException("Version not found: $id2") }

        val lines1 = v1.content.lines()
        val lines2 = v2.content.lines()
        val diff = buildString {
            val maxLines = maxOf(lines1.size, lines2.size)
            for (i in 0 until maxLines) {
                val l1 = lines1.getOrNull(i)
                val l2 = lines2.getOrNull(i)
                when {
                    l1 == l2 -> appendLine("  $l1")
                    l1 == null -> appendLine("+ $l2")
                    l2 == null -> appendLine("- $l1")
                    else -> {
                        appendLine("- $l1")
                        appendLine("+ $l2")
                    }
                }
            }
        }

        return DiffDto(
            versionFrom = v1.toDto(),
            versionTo = v2.toDto(),
            changes = diff
        )
    }

    @Transactional
    fun rollback(id: Long, username: String): VersionDto {
        val version = versionRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Version not found: $id") }

        val request = CreateVersionRequest(
            content = version.content,
            changelog = "Rollback to ${version.versionNumber}"
        )
        return create(version.skill.id, request, username)
    }

    private fun Version.toDto() = VersionDto(
        id = id,
        skillId = skill.id,
        versionNumber = versionNumber,
        content = content,
        changelog = changelog,
        status = status.name,
        author = author.toDto(),
        gitCommitSha = gitCommitSha,
        createdAt = createdAt
    )
}
