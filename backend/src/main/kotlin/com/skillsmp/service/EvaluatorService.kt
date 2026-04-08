package com.skillsmp.service

import com.skillsmp.domain.EvaluatorConfig
import com.skillsmp.domain.EvaluatorType
import com.skillsmp.dto.CreateEvaluatorRequest
import com.skillsmp.dto.EvaluatorDto
import com.skillsmp.dto.UpdateEvaluatorRequest
import com.skillsmp.repository.EvaluatorConfigRepository
import com.skillsmp.repository.ProjectRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class EvaluatorService(
    private val evaluatorConfigRepository: EvaluatorConfigRepository,
    private val projectRepository: ProjectRepository
) {
    @Transactional
    fun create(request: CreateEvaluatorRequest): EvaluatorDto {
        val project = projectRepository.findById(request.projectId)
            .orElseThrow { EntityNotFoundException("Project not found: ${request.projectId}") }

        val evaluator = EvaluatorConfig().apply {
            name = request.name
            type = EvaluatorType.valueOf(request.type)
            configuration = request.configuration
            this.project = project
        }
        return evaluatorConfigRepository.save(evaluator).toDto()
    }

    fun getById(id: Long): EvaluatorDto {
        return evaluatorConfigRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Evaluator not found: $id") }
            .toDto()
    }

    fun getByProjectId(projectId: Long): List<EvaluatorDto> {
        return evaluatorConfigRepository.findByProjectId(projectId).map { it.toDto() }
    }

    @Transactional
    fun update(id: Long, request: UpdateEvaluatorRequest): EvaluatorDto {
        val evaluator = evaluatorConfigRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Evaluator not found: $id") }

        evaluator.name = request.name
        evaluator.type = EvaluatorType.valueOf(request.type)
        evaluator.configuration = request.configuration

        return evaluatorConfigRepository.save(evaluator).toDto()
    }

    @Transactional
    fun delete(id: Long) {
        if (!evaluatorConfigRepository.existsById(id)) {
            throw EntityNotFoundException("Evaluator not found: $id")
        }
        evaluatorConfigRepository.deleteById(id)
    }

    private fun EvaluatorConfig.toDto() = EvaluatorDto(
        id = id,
        name = name,
        type = type.name,
        configuration = configuration,
        projectId = project.id,
        createdAt = createdAt
    )
}
