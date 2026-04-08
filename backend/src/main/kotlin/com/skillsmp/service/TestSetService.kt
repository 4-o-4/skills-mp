package com.skillsmp.service

import com.skillsmp.domain.TestCase
import com.skillsmp.domain.TestSet
import com.skillsmp.dto.*
import com.skillsmp.repository.ProjectRepository
import com.skillsmp.repository.TestCaseRepository
import com.skillsmp.repository.TestSetRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class TestSetService(
    private val testSetRepository: TestSetRepository,
    private val testCaseRepository: TestCaseRepository,
    private val projectRepository: ProjectRepository
) {
    @Transactional
    fun create(request: CreateTestSetRequest): TestSetDto {
        val project = projectRepository.findById(request.projectId)
            .orElseThrow { EntityNotFoundException("Project not found: ${request.projectId}") }

        val testSet = TestSet().apply {
            name = request.name
            description = request.description
            this.project = project
        }
        return testSetRepository.save(testSet).toDto()
    }

    fun getById(id: Long): TestSetDetailDto {
        val testSet = testSetRepository.findById(id)
            .orElseThrow { EntityNotFoundException("TestSet not found: $id") }
        val cases = testCaseRepository.findByTestSetId(id)
        return testSet.toDetailDto(cases)
    }

    fun getByProjectId(projectId: Long): List<TestSetDto> {
        return testSetRepository.findByProjectId(projectId).map { it.toDto() }
    }

    @Transactional
    fun update(id: Long, request: UpdateTestSetRequest): TestSetDto {
        val testSet = testSetRepository.findById(id)
            .orElseThrow { EntityNotFoundException("TestSet not found: $id") }

        testSet.name = request.name
        testSet.description = request.description
        testSet.updatedAt = LocalDateTime.now()

        return testSetRepository.save(testSet).toDto()
    }

    @Transactional
    fun delete(id: Long) {
        if (!testSetRepository.existsById(id)) {
            throw EntityNotFoundException("TestSet not found: $id")
        }
        testSetRepository.deleteById(id)
    }

    @Transactional
    fun addCase(testSetId: Long, request: CreateTestCaseRequest): TestCaseDto {
        val testSet = testSetRepository.findById(testSetId)
            .orElseThrow { EntityNotFoundException("TestSet not found: $testSetId") }

        val testCase = TestCase().apply {
            this.testSet = testSet
            input = request.input
            expectedOutput = request.expectedOutput
            metadata = request.metadata ?: "{}"
        }

        return testCaseRepository.save(testCase).toDto()
    }

    @Transactional
    fun removeCase(caseId: Long) {
        if (!testCaseRepository.existsById(caseId)) {
            throw EntityNotFoundException("TestCase not found: $caseId")
        }
        testCaseRepository.deleteById(caseId)
    }

    fun getCases(testSetId: Long): List<TestCaseDto> {
        return testCaseRepository.findByTestSetId(testSetId).map { it.toDto() }
    }

    private fun TestSet.toDto(): TestSetDto {
        val caseCount = testCaseRepository.findByTestSetId(id).size
        return TestSetDto(
            id = id,
            name = name,
            description = description,
            projectId = project.id,
            caseCount = caseCount,
            createdAt = createdAt
        )
    }

    private fun TestSet.toDetailDto(cases: List<TestCase>): TestSetDetailDto {
        return TestSetDetailDto(
            id = id,
            name = name,
            description = description,
            projectId = project.id,
            caseCount = cases.size,
            createdAt = createdAt,
            cases = cases.map { it.toDto() }
        )
    }

    private fun TestCase.toDto() = TestCaseDto(
        id = id,
        testSetId = testSet.id,
        input = input,
        expectedOutput = expectedOutput,
        metadata = metadata
    )
}
