package com.skillsmp.repository

import com.skillsmp.domain.EvaluatorConfig
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface EvaluatorConfigRepository : JpaRepository<EvaluatorConfig, Long> {
    fun findByProjectId(projectId: Long): List<EvaluatorConfig>
}
