package com.skillsmp.repository

import com.skillsmp.domain.TestCase
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TestCaseRepository : JpaRepository<TestCase, Long> {
    fun findByTestSetId(testSetId: Long): List<TestCase>
}
