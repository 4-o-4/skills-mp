package com.skillsmp.repository

import com.skillsmp.domain.Tag
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface TagRepository : JpaRepository<Tag, Long> {
    fun findByNameIgnoreCase(name: String): Optional<Tag>
}
