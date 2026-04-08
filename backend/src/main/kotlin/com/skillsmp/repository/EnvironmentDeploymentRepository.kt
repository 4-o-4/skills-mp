package com.skillsmp.repository

import com.skillsmp.domain.EnvironmentDeployment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface EnvironmentDeploymentRepository : JpaRepository<EnvironmentDeployment, Long> {
    fun findByEnvironmentIdOrderByDeployedAtDesc(environmentId: Long): List<EnvironmentDeployment>
}
