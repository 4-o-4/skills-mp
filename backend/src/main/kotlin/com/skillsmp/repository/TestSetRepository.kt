package com.skillsmp.repository

import com.skillsmp.domain.TestSet
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TestSetRepository : JpaRepository<TestSet, Long> {
    fun findByProjectId(projectId: Long): List<TestSet>
}
