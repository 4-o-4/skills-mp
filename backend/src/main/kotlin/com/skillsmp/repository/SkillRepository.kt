package com.skillsmp.repository

import com.skillsmp.domain.EntityStatus
import com.skillsmp.domain.Skill
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SkillRepository : JpaRepository<Skill, Long> {
    fun findByProjectId(projectId: Long): List<Skill>
    fun findByProjectIdAndStatus(projectId: Long, status: EntityStatus): List<Skill>
    fun findByStatus(status: EntityStatus): List<Skill>
    fun findByNameContainingIgnoreCase(name: String): List<Skill>
}
