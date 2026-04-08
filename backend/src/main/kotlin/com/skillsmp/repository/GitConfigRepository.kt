package com.skillsmp.repository

import com.skillsmp.domain.GitConfig
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface GitConfigRepository : JpaRepository<GitConfig, Long> {
    fun findByProjectId(projectId: Long): Optional<GitConfig>
}
