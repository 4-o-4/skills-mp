package com.skillsmp.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "evaluator_results")
class EvaluatorResult(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_run_id", nullable = false)
    @JsonIgnore
    var testRun: TestRun = TestRun(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_case_id", nullable = false)
    @JsonIgnore
    var testCase: TestCase = TestCase(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluator_id", nullable = false)
    @JsonIgnore
    var evaluator: EvaluatorConfig = EvaluatorConfig(),

    @Column(name = "raw_output")
    var rawOutput: String? = null,

    var score: Double? = null,

    @Column(nullable = false)
    var passed: Boolean = false,

    var explanation: String? = null,

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()
)
