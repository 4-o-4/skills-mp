package com.skillsmp.repository

import com.skillsmp.domain.AuditLog
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AuditLogRepository : JpaRepository<AuditLog, Long> {
    fun findByEntityTypeAndEntityId(entityType: String, entityId: Long): List<AuditLog>
}
