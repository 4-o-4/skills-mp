package com.skillsmp.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "test_runs")
class TestRun(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_set_id", nullable = false)
    @JsonIgnore
    var testSet: TestSet = TestSet(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "version_id", nullable = false)
    @JsonIgnore
    var version: Version = Version(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id")
    @JsonIgnore
    var variant: Variant? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: TestRunStatus = TestRunStatus.QUEUED,

    @Column(name = "total_cases", nullable = false)
    var totalCases: Int = 0,

    @Column(name = "passed_cases", nullable = false)
    var passedCases: Int = 0,

    @Column(name = "failed_cases", nullable = false)
    var failedCases: Int = 0,

    @Column(name = "started_at")
    var startedAt: LocalDateTime? = null,

    @Column(name = "completed_at")
    var completedAt: LocalDateTime? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", nullable = false)
    @JsonIgnore
    var createdBy: User = User(),

    @OneToMany(mappedBy = "testRun", fetch = FetchType.LAZY)
    @JsonIgnore
    var results: MutableList<EvaluatorResult> = mutableListOf(),

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()
)
