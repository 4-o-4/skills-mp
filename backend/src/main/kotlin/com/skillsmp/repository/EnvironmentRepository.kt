package com.skillsmp.repository

import com.skillsmp.domain.Environment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface EnvironmentRepository : JpaRepository<Environment, Long> {
    fun findByProjectId(projectId: Long): List<Environment>
}
