package com.skillsmp.repository

import com.skillsmp.domain.Variant
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface VariantRepository : JpaRepository<Variant, Long> {
    fun findBySkillId(skillId: Long): List<Variant>
}
