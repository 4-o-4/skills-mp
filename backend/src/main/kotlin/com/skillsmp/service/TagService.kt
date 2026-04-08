package com.skillsmp.service

import com.skillsmp.domain.Tag
import com.skillsmp.dto.CreateTagRequest
import com.skillsmp.dto.TagDto
import com.skillsmp.repository.SkillRepository
import com.skillsmp.repository.TagRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TagService(
    private val tagRepository: TagRepository,
    private val skillRepository: SkillRepository
) {
    @Transactional
    fun create(request: CreateTagRequest): TagDto {
        val existing = tagRepository.findByNameIgnoreCase(request.name)
        if (existing.isPresent) {
            throw IllegalArgumentException("Tag already exists: ${request.name}")
        }

        val tag = Tag().apply {
            name = request.name
            color = request.color ?: "#3B82F6"
        }
        return tagRepository.save(tag).toDto()
    }

    fun getAll(): List<TagDto> {
        return tagRepository.findAll().map { it.toDto() }
    }

    @Transactional
    fun delete(id: Long) {
        if (!tagRepository.existsById(id)) {
            throw EntityNotFoundException("Tag not found: $id")
        }
        tagRepository.deleteById(id)
    }

    @Transactional
    fun assignToSkill(skillId: Long, tagId: Long) {
        val skill = skillRepository.findById(skillId)
            .orElseThrow { EntityNotFoundException("Skill not found: $skillId") }
        val tag = tagRepository.findById(tagId)
            .orElseThrow { EntityNotFoundException("Tag not found: $tagId") }
        skill.tags.add(tag)
        skillRepository.save(skill)
    }

    @Transactional
    fun removeFromSkill(skillId: Long, tagId: Long) {
        val skill = skillRepository.findById(skillId)
            .orElseThrow { EntityNotFoundException("Skill not found: $skillId") }
        skill.tags.removeIf { it.id == tagId }
        skillRepository.save(skill)
    }
}

fun Tag.toDto() = TagDto(id = id, name = name, color = color)
