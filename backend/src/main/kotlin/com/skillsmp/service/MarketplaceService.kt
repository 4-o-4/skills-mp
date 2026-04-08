package com.skillsmp.service

import com.skillsmp.domain.EntityStatus
import com.skillsmp.dto.MarketplaceSkillDto
import com.skillsmp.repository.ReviewRepository
import com.skillsmp.repository.SkillRepository
import com.skillsmp.repository.VersionRepository
import org.springframework.stereotype.Service

@Service
class MarketplaceService(
    private val skillRepository: SkillRepository,
    private val versionRepository: VersionRepository,
    private val reviewRepository: ReviewRepository
) {
    fun getPublishedSkills(): List<MarketplaceSkillDto> {
        return skillRepository.findByStatus(EntityStatus.PUBLISHED).map { skill ->
            val versions = versionRepository.findBySkillIdOrderByCreatedAtDesc(skill.id)
            val latestVersion = versions.firstOrNull()
            val allReviews = versions.flatMap { v -> reviewRepository.findByVersionId(v.id) }
            val avgRating = if (allReviews.isNotEmpty()) {
                allReviews.mapNotNull { it.rating }.average()
            } else {
                0.0
            }

            MarketplaceSkillDto(
                id = skill.id,
                name = skill.name,
                description = skill.description,
                author = skill.owner.toDto(),
                tags = skill.tags.map { it.toDto() },
                currentVersion = latestVersion?.versionNumber,
                rating = avgRating,
                downloadCount = 0,
                publishedAt = skill.updatedAt
            )
        }
    }

    fun getSkillById(id: Long): MarketplaceSkillDto {
        val skill = skillRepository.findById(id)
            .orElseThrow { jakarta.persistence.EntityNotFoundException("Skill not found: $id") }
        val versions = versionRepository.findBySkillIdOrderByCreatedAtDesc(skill.id)
        val latestVersion = versions.firstOrNull()
        val allReviews = versions.flatMap { v -> reviewRepository.findByVersionId(v.id) }
        val avgRating = if (allReviews.isNotEmpty()) {
            allReviews.mapNotNull { it.rating }.average()
        } else {
            0.0
        }

        return MarketplaceSkillDto(
            id = skill.id,
            name = skill.name,
            description = skill.description,
            author = skill.owner.toDto(),
            tags = skill.tags.map { it.toDto() },
            currentVersion = latestVersion?.versionNumber,
            rating = avgRating,
            downloadCount = 0,
            publishedAt = skill.updatedAt
        )
    }
}
