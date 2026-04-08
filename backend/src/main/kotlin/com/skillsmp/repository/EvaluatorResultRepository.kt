package com.skillsmp.repository

import com.skillsmp.domain.EvaluatorResult
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface EvaluatorResultRepository : JpaRepository<EvaluatorResult, Long> {
    fun findByTestRunId(testRunId: Long): List<EvaluatorResult>
}
