package com.skillsmp.service

import com.skillsmp.domain.Variant
import com.skillsmp.dto.*
import com.skillsmp.repository.SkillRepository
import com.skillsmp.repository.TestCaseRepository
import com.skillsmp.repository.VariantRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class VariantService(
    private val variantRepository: VariantRepository,
    private val skillRepository: SkillRepository,
    private val testCaseRepository: TestCaseRepository
) {
    @Transactional
    fun create(skillId: Long, request: CreateVariantRequest): VariantDto {
        val skill = skillRepository.findById(skillId)
            .orElseThrow { EntityNotFoundException("Skill not found: $skillId") }

        val variant = Variant().apply {
            this.skill = skill
            name = request.name
            modelProvider = request.modelProvider
            modelName = request.modelName
            systemPrompt = request.systemPrompt
            temperature = request.temperature
            maxTokens = request.maxTokens
            config = request.config ?: "{}"
        }
        return variantRepository.save(variant).toDto()
    }

    fun getById(id: Long): VariantDto {
        return variantRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Variant not found: $id") }
            .toDto()
    }

    fun getBySkillId(skillId: Long): List<VariantDto> {
        return variantRepository.findBySkillId(skillId).map { it.toDto() }
    }

    @Transactional
    fun update(id: Long, request: CreateVariantRequest): VariantDto {
        val variant = variantRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Variant not found: $id") }

        variant.name = request.name
        variant.modelProvider = request.modelProvider
        variant.modelName = request.modelName
        variant.systemPrompt = request.systemPrompt
        variant.temperature = request.temperature
        variant.maxTokens = request.maxTokens
        request.config?.let { variant.config = it }

        return variantRepository.save(variant).toDto()
    }

    @Transactional
    fun delete(id: Long) {
        if (!variantRepository.existsById(id)) {
            throw EntityNotFoundException("Variant not found: $id")
        }
        variantRepository.deleteById(id)
    }

    fun compare(request: CompareRequest): CompareResultDto {
        val variants = request.variantIds.map { id ->
            variantRepository.findById(id)
                .orElseThrow { EntityNotFoundException("Variant not found: $id") }
        }
        val testCases = testCaseRepository.findByTestSetId(request.testSetId)

        val results = variants.flatMap { variant ->
            testCases.map { tc ->
                CompareResultItem(
                    variantId = variant.id,
                    testCaseId = tc.id,
                    output = "Mock output for variant ${variant.name}",
                    latency = (100..500).random().toLong(),
                    score = (70..100).random().toDouble() / 100.0,
                    passed = true
                )
            }
        }

        return CompareResultDto(
            variants = variants.map { it.toDto() },
            results = results
        )
    }

    private fun Variant.toDto() = VariantDto(
        id = id,
        skillId = skill.id,
        name = name,
        modelProvider = modelProvider,
        modelName = modelName,
        systemPrompt = systemPrompt,
        temperature = temperature,
        maxTokens = maxTokens,
        config = config,
        createdAt = createdAt
    )
}
