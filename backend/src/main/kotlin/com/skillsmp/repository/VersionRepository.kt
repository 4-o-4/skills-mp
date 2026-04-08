package com.skillsmp.repository

import com.skillsmp.domain.EntityStatus
import com.skillsmp.domain.Version
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface VersionRepository : JpaRepository<Version, Long> {
    fun findBySkillIdOrderByCreatedAtDesc(skillId: Long): List<Version>
    fun findBySkillIdAndStatus(skillId: Long, status: EntityStatus): List<Version>
}
