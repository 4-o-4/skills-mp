package com.skillsmp.service

import com.skillsmp.domain.AuditLog
import com.skillsmp.repository.AuditLogRepository
import com.skillsmp.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuditService(
    private val auditLogRepository: AuditLogRepository,
    private val userRepository: UserRepository
) {
    @Transactional
    fun log(userId: Long?, action: String, entityType: String, entityId: Long?, details: String? = null) {
        val user = userId?.let { userRepository.findById(it).orElse(null) }
        val auditLog = AuditLog().apply {
            this.user = user
            this.action = action
            this.entityType = entityType
            this.entityId = entityId
            this.details = details
        }
        auditLogRepository.save(auditLog)
    }

    fun getByEntity(entityType: String, entityId: Long): List<AuditLog> {
        return auditLogRepository.findByEntityTypeAndEntityId(entityType, entityId)
    }
}
