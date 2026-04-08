package com.skillsmp.service

import com.skillsmp.domain.EntityStatus
import com.skillsmp.domain.Review
import com.skillsmp.domain.ReviewStatus
import com.skillsmp.dto.CreateReviewRequest
import com.skillsmp.dto.ReviewDto
import com.skillsmp.repository.ReviewRepository
import com.skillsmp.repository.UserRepository
import com.skillsmp.repository.VersionRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ReviewService(
    private val reviewRepository: ReviewRepository,
    private val versionRepository: VersionRepository,
    private val userRepository: UserRepository
) {
    @Transactional
    fun create(versionId: Long, request: CreateReviewRequest, username: String): ReviewDto {
        val version = versionRepository.findById(versionId)
            .orElseThrow { EntityNotFoundException("Version not found: $versionId") }
        val author = userRepository.findByUsername(username)
            .orElseThrow { EntityNotFoundException("User not found") }

        val review = Review().apply {
            this.version = version
            this.author = author
            rating = request.rating
            comment = request.comment
            status = request.status?.let { ReviewStatus.valueOf(it) } ?: ReviewStatus.PENDING
        }

        val saved = reviewRepository.save(review)

        if (saved.status == ReviewStatus.APPROVED) {
            val allReviews = reviewRepository.findByVersionId(versionId)
            val allApproved = allReviews.all { it.status == ReviewStatus.APPROVED }
            if (allApproved && allReviews.size > 0) {
                version.status = EntityStatus.VERIFIED
                versionRepository.save(version)
            }
        }

        return saved.toDto()
    }

    fun getByVersionId(versionId: Long): List<ReviewDto> {
        return reviewRepository.findByVersionId(versionId).map { it.toDto() }
    }

    private fun Review.toDto() = ReviewDto(
        id = id,
        versionId = version.id,
        author = author.toDto(),
        rating = rating,
        comment = comment,
        status = status.name,
        createdAt = createdAt
    )
}
