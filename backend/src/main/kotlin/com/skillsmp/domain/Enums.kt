package com.skillsmp.domain

enum class UserRole { ADMIN, EDITOR, REVIEWER, VIEWER }
enum class EntityStatus { DRAFT, VERIFIED, PUBLISHED, ARCHIVED }
enum class TestRunStatus { QUEUED, RUNNING, COMPLETED, FAILED, CANCELLED }
enum class EvaluatorType { EXACT_MATCH, JSON_VALIDATION, REGEXP, CODE, LLM_AS_JUDGE }
enum class ReviewStatus { PENDING, APPROVED, REJECTED }
