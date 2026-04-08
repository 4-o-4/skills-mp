package com.skillsmp.dto

import java.time.LocalDateTime

data class LoginRequest(val username: String, val password: String)
data class RegisterRequest(val username: String, val email: String, val password: String)
data class AuthResponse(val token: String, val user: UserDto)

data class UserDto(
    val id: Long,
    val username: String,
    val email: String,
    val role: String,
    val createdAt: LocalDateTime
)

data class ProjectDto(
    val id: Long,
    val name: String,
    val description: String?,
    val owner: UserDto,
    val status: String,
    val skillCount: Int,
    val updatedAt: LocalDateTime,
    val createdAt: LocalDateTime
)

data class CreateProjectRequest(val name: String, val description: String?)

open class SkillDto(
    val id: Long,
    val name: String,
    val description: String?,
    val status: String,
    val owner: UserDto,
    val tags: List<TagDto>,
    val projectId: Long,
    val updatedAt: LocalDateTime,
    val createdAt: LocalDateTime
)

class SkillDetailDto(
    id: Long,
    name: String,
    description: String?,
    status: String,
    owner: UserDto,
    tags: List<TagDto>,
    projectId: Long,
    updatedAt: LocalDateTime,
    createdAt: LocalDateTime,
    val promptBody: String?,
    val metadata: String?,
    val latestVersion: VersionDto?,
    val variantCount: Int,
    val testRunCount: Int
) : SkillDto(id, name, description, status, owner, tags, projectId, updatedAt, createdAt)

data class CreateSkillRequest(
    val name: String,
    val description: String?,
    val promptBody: String?,
    val metadata: String? = null,
    val tagIds: List<Long>? = null
)

data class UpdateSkillRequest(
    val name: String? = null,
    val description: String? = null,
    val promptBody: String? = null,
    val metadata: String? = null,
    val status: String? = null
)

data class VersionDto(
    val id: Long,
    val skillId: Long,
    val versionNumber: String,
    val content: String,
    val changelog: String?,
    val status: String,
    val author: UserDto,
    val gitCommitSha: String?,
    val createdAt: LocalDateTime
)

data class CreateVersionRequest(val content: String, val changelog: String?)

data class DiffDto(
    val versionFrom: VersionDto,
    val versionTo: VersionDto,
    val changes: String
)

data class VariantDto(
    val id: Long,
    val skillId: Long,
    val name: String,
    val modelProvider: String?,
    val modelName: String?,
    val systemPrompt: String?,
    val temperature: Double,
    val maxTokens: Int,
    val config: String,
    val createdAt: LocalDateTime
)

data class CreateVariantRequest(
    val name: String,
    val modelProvider: String?,
    val modelName: String?,
    val systemPrompt: String?,
    val temperature: Double = 0.7,
    val maxTokens: Int = 1024,
    val config: String? = null
)

data class TagDto(val id: Long, val name: String, val color: String)
data class CreateTagRequest(val name: String, val color: String? = null)

open class TestSetDto(
    val id: Long,
    val name: String,
    val description: String?,
    val projectId: Long,
    val caseCount: Int,
    val createdAt: LocalDateTime
)

class TestSetDetailDto(
    id: Long,
    name: String,
    description: String?,
    projectId: Long,
    caseCount: Int,
    createdAt: LocalDateTime,
    val cases: List<TestCaseDto>
) : TestSetDto(id, name, description, projectId, caseCount, createdAt)

data class CreateTestSetRequest(val name: String, val description: String?, val projectId: Long)

data class UpdateTestSetRequest(val name: String, val description: String?)

data class TestCaseDto(
    val id: Long,
    val testSetId: Long,
    val input: String,
    val expectedOutput: String?,
    val metadata: String?
)

data class CreateTestCaseRequest(
    val input: String,
    val expectedOutput: String?,
    val metadata: String? = null
)

open class TestRunDto(
    val id: Long,
    val testSetId: Long,
    val testSetName: String,
    val versionId: Long,
    val variantId: Long?,
    val status: String,
    val totalCases: Int,
    val passedCases: Int,
    val failedCases: Int,
    val startedAt: LocalDateTime?,
    val completedAt: LocalDateTime?,
    val createdBy: UserDto,
    val createdAt: LocalDateTime
)

class TestRunDetailDto(
    id: Long,
    testSetId: Long,
    testSetName: String,
    versionId: Long,
    variantId: Long?,
    status: String,
    totalCases: Int,
    passedCases: Int,
    failedCases: Int,
    startedAt: LocalDateTime?,
    completedAt: LocalDateTime?,
    createdBy: UserDto,
    createdAt: LocalDateTime,
    val results: List<EvaluatorResultDto>
) : TestRunDto(
    id,
    testSetId,
    testSetName,
    versionId,
    variantId,
    status,
    totalCases,
    passedCases,
    failedCases,
    startedAt,
    completedAt,
    createdBy,
    createdAt
)

data class CreateTestRunRequest(
    val testSetId: Long,
    val versionId: Long,
    val variantId: Long? = null,
    val evaluatorIds: List<Long>
)

data class EvaluatorResultDto(
    val id: Long,
    val testRunId: Long,
    val testCaseId: Long,
    val evaluatorId: Long,
    val evaluatorName: String,
    val rawOutput: String?,
    val score: Double?,
    val passed: Boolean,
    val explanation: String?
)

data class EvaluatorDto(
    val id: Long,
    val name: String,
    val type: String,
    val configuration: String,
    val projectId: Long,
    val createdAt: LocalDateTime
)

data class CreateEvaluatorRequest(
    val name: String,
    val type: String,
    val configuration: String,
    val projectId: Long
)

data class UpdateEvaluatorRequest(val name: String, val type: String, val configuration: String)

data class ReviewDto(
    val id: Long,
    val versionId: Long,
    val author: UserDto,
    val rating: Int?,
    val comment: String?,
    val status: String,
    val createdAt: LocalDateTime
)

data class CreateReviewRequest(
    val rating: Int? = null,
    val comment: String? = null,
    val status: String? = null
)

data class EnvironmentDto(
    val id: Long,
    val name: String,
    val description: String?,
    val projectId: Long,
    val activeVersionId: Long?,
    val activeVersionNumber: String?,
    val createdAt: LocalDateTime
)

data class CreateEnvironmentRequest(val name: String, val description: String?, val projectId: Long)
data class DeployRequest(val versionId: Long)

data class DeploymentDto(
    val id: Long,
    val environmentId: Long,
    val versionId: Long,
    val versionNumber: String,
    val deployedBy: UserDto,
    val deployedAt: LocalDateTime
)

data class ReleaseDto(
    val id: Long,
    val projectId: Long,
    val versionId: Long,
    val versionNumber: String,
    val environmentId: Long,
    val environmentName: String,
    val gitTag: String?,
    val notes: String?,
    val createdBy: UserDto,
    val createdAt: LocalDateTime
)

data class CreateReleaseRequest(
    val versionId: Long,
    val environmentId: Long,
    val gitTag: String? = null,
    val notes: String? = null
)

data class GitConfigDto(
    val id: Long,
    val projectId: Long,
    val repoUrl: String,
    val branch: String,
    val connected: Boolean
)

data class ConnectGitRequest(val repoUrl: String, val branch: String = "main", val token: String? = null)
data class GitSyncResultDto(val success: Boolean, val commitSha: String?, val message: String)
data class GitStatusDto(val connected: Boolean, val lastSync: LocalDateTime?, val repoUrl: String?, val branch: String?)

data class MarketplaceSkillDto(
    val id: Long,
    val name: String,
    val description: String?,
    val author: UserDto,
    val tags: List<TagDto>,
    val currentVersion: String?,
    val rating: Double,
    val downloadCount: Int,
    val publishedAt: LocalDateTime?
)

data class StatusUpdateRequest(val status: String)

data class CompareRequest(val variantIds: List<Long>, val testSetId: Long)

data class CompareResultDto(
    val variants: List<VariantDto>,
    val results: List<CompareResultItem>
)

data class CompareResultItem(
    val variantId: Long,
    val testCaseId: Long,
    val output: String,
    val latency: Long,
    val score: Double,
    val passed: Boolean
)
