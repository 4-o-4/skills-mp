package com.skillsmp.repository

import com.skillsmp.domain.TestRun
import com.skillsmp.domain.TestRunStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TestRunRepository : JpaRepository<TestRun, Long> {
    fun findByVersionId(versionId: Long): List<TestRun>
    fun findByTestSetId(testSetId: Long): List<TestRun>
    fun findByStatus(status: TestRunStatus): List<TestRun>
    fun findByCreatedById(createdById: Long): List<TestRun>
}
