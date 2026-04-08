package com.skillsmp.controller

import com.skillsmp.dto.CreateReviewRequest
import com.skillsmp.dto.ReviewDto
import com.skillsmp.service.ReviewService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.security.Principal

@RestController
class ReviewController(
    private val reviewService: ReviewService
) {
    @GetMapping("/api/versions/{versionId}/reviews")
    fun getByVersionId(@PathVariable versionId: Long): ResponseEntity<List<ReviewDto>> {
        return ResponseEntity.ok(reviewService.getByVersionId(versionId))
    }

    @PostMapping("/api/versions/{versionId}/reviews")
    fun create(
        @PathVariable versionId: Long,
        @RequestBody request: CreateReviewRequest,
        principal: Principal
    ): ResponseEntity<ReviewDto> {
        return ResponseEntity.ok(reviewService.create(versionId, request, principal.name))
    }
}
