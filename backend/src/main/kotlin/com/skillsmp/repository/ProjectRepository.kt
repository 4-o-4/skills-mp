package com.skillsmp.repository

import com.skillsmp.domain.Project
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectRepository : JpaRepository<Project, Long> {
    fun findByOwnerId(ownerId: Long): List<Project>
    fun findByStatus(status: String): List<Project>
}
