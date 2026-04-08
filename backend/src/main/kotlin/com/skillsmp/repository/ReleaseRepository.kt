package com.skillsmp.repository

import com.skillsmp.domain.Release
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ReleaseRepository : JpaRepository<Release, Long> {
    fun findByProjectId(projectId: Long): List<Release>
    fun findByVersionId(versionId: Long): List<Release>
}
