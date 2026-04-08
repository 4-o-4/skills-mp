package com.skillsmp.repository

import com.skillsmp.domain.Review
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ReviewRepository : JpaRepository<Review, Long> {
    fun findByVersionId(versionId: Long): List<Review>
    fun findByAuthorId(authorId: Long): List<Review>
}
