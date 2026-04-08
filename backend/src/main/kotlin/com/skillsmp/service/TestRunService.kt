package com.skillsmp.service

import com.skillsmp.domain.*
import com.skillsmp.dto.*
import com.skillsmp.repository.*
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class TestRunService(
    private val testRunRepository: TestRunRepository,
    private val testSetRepository: TestSetRepository,
    private val testCaseRepository: TestCaseRepository,
    private val versionRepository: VersionRepository,
    private val variantRepository: VariantRepository,
    private val evaluatorConfigRepository: EvaluatorConfigRepository,
    private val evaluatorResultRepository: EvaluatorResultRepository,
    private val userRepository: UserRepository
) {
    @Transactional
    fun create(request: CreateTestRunRequest, username: String): TestRunDto {
        val testSet = testSetRepository.findById(request.testSetId)
            .orElseThrow { EntityNotFoundException("TestSet not found: ${request.testSetId}") }
        val version = versionRepository.findById(request.versionId)
            .orElseThrow { EntityNotFoundException("Version not found: ${request.versionId}") }
        val variant = request.variantId?.let {
            variantRepository.findById(it).orElseThrow { EntityNotFoundException("Variant not found: $it") }
        }
        val user = userRepository.findByUsername(username)
            .orElseThrow { EntityNotFoundException("User not found") }

        val evaluators = request.evaluatorIds.map { id ->
            evaluatorConfigRepository.findById(id)
                .orElseThrow { EntityNotFoundException("Evaluator not found: $id") }
        }

        val cases = testCaseRepository.findByTestSetId(request.testSetId)

        val testRun = TestRun().apply {
            this.testSet = testSet
            this.version = version
            this.variant = variant
            this.createdBy = user
            totalCases = cases.size
            status = TestRunStatus.RUNNING
            startedAt = LocalDateTime.now()
        }
        val savedRun = testRunRepository.save(testRun)

        var passedCount = 0
        var failedCount = 0

        for (tc in cases) {
            for (evaluator in evaluators) {
                val evalOutput = executeEvaluator(evaluator, tc, version)
                val evalResult = EvaluatorResult().apply {
                    this.testRun = savedRun
                    this.testCase = tc
                    this.evaluator = evaluator
                    rawOutput = evalOutput.rawOutput
                    score = evalOutput.score
                    this.passed = evalOutput.passed
                    explanation = evalOutput.explanation
                }
                evaluatorResultRepository.save(evalResult)

                if (evalOutput.passed) passedCount++ else failedCount++
            }
        }

        savedRun.passedCases = passedCount
        savedRun.failedCases = failedCount
        savedRun.status = TestRunStatus.COMPLETED
        savedRun.completedAt = LocalDateTime.now()
        val finishedRun = testRunRepository.save(savedRun)

        return finishedRun.toDto()
    }

    fun getById(id: Long): TestRunDetailDto {
        val run = testRunRepository.findById(id)
            .orElseThrow { EntityNotFoundException("TestRun not found: $id") }
        val results = evaluatorResultRepository.findByTestRunId(id)
        return run.toDetailDto(results)
    }

    fun getByVersionId(versionId: Long): List<TestRunDto> {
        return testRunRepository.findByVersionId(versionId).map { it.toDto() }
    }

    @Transactional
    fun cancel(id: Long): TestRunDto {
        val run = testRunRepository.findById(id)
            .orElseThrow { EntityNotFoundException("TestRun not found: $id") }

        if (run.status == TestRunStatus.COMPLETED || run.status == TestRunStatus.CANCELLED) {
            throw IllegalStateException("TestRun already ${run.status}")
        }

        run.status = TestRunStatus.CANCELLED
        run.completedAt = LocalDateTime.now()
        return testRunRepository.save(run).toDto()
    }

    private fun executeEvaluator(
        evaluator: EvaluatorConfig,
        testCase: TestCase,
        version: Version
    ): EvalResult {
        val output = version.content
        val expected = testCase.expectedOutput ?: ""

        return when (evaluator.type) {
            EvaluatorType.EXACT_MATCH -> {
                val passed = output.trim() == expected.trim()
                EvalResult(output, if (passed) 1.0 else 0.0, passed, "Exact match: $passed")
            }

            EvaluatorType.REGEXP -> {
                val pattern = expected.toRegex()
                val passed = pattern.containsMatchIn(output)
                EvalResult(output, if (passed) 1.0 else 0.0, passed, "Regex match: $passed")
            }

            EvaluatorType.JSON_VALIDATION -> {
                val passed = try {
                    com.fasterxml.jackson.databind.ObjectMapper().readTree(output)
                    true
                } catch (e: Exception) {
                    false
                }
                EvalResult(output, if (passed) 1.0 else 0.0, passed, "Valid JSON: $passed")
            }

            EvaluatorType.CODE -> {
                val passed = output.isNotBlank()
                EvalResult(output, if (passed) 1.0 else 0.0, passed, "Code evaluation: non-empty output")
            }

            EvaluatorType.LLM_AS_JUDGE -> {
                EvalResult(output, 0.8, true, "LLM judge mock: acceptable quality")
            }
        }
    }

    private data class EvalResult(
        val rawOutput: String?,
        val score: Double?,
        val passed: Boolean,
        val explanation: String?
    )

    private fun TestRun.toDto() = TestRunDto(
        id = id,
        testSetId = testSet.id,
        testSetName = testSet.name,
        versionId = version.id,
        variantId = variant?.id,
        status = status.name,
        totalCases = totalCases,
        passedCases = passedCases,
        failedCases = failedCases,
        startedAt = startedAt,
        completedAt = completedAt,
        createdBy = createdBy.toDto(),
        createdAt = createdAt
    )

    private fun TestRun.toDetailDto(results: List<EvaluatorResult>) = TestRunDetailDto(
        id = id,
        testSetId = testSet.id,
        testSetName = testSet.name,
        versionId = version.id,
        variantId = variant?.id,
        status = status.name,
        totalCases = totalCases,
        passedCases = passedCases,
        failedCases = failedCases,
        startedAt = startedAt,
        completedAt = completedAt,
        createdBy = createdBy.toDto(),
        createdAt = createdAt,
        results = results.map { r ->
            EvaluatorResultDto(
                id = r.id,
                testRunId = r.testRun.id,
                testCaseId = r.testCase.id,
                evaluatorId = r.evaluator.id,
                evaluatorName = r.evaluator.name,
                rawOutput = r.rawOutput,
                score = r.score,
                passed = r.passed,
                explanation = r.explanation
            )
        }
    )
}
