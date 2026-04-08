package com.skillsmp.controller

import com.skillsmp.dto.CompareRequest
import com.skillsmp.dto.CompareResultDto
import com.skillsmp.dto.CreateVariantRequest
import com.skillsmp.dto.VariantDto
import com.skillsmp.service.VariantService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class VariantController(
    private val variantService: VariantService
) {
    @GetMapping("/api/skills/{skillId}/variants")
    fun getBySkillId(@PathVariable skillId: Long): ResponseEntity<List<VariantDto>> {
        return ResponseEntity.ok(variantService.getBySkillId(skillId))
    }

    @PostMapping("/api/skills/{skillId}/variants")
    fun create(
        @PathVariable skillId: Long,
        @RequestBody request: CreateVariantRequest
    ): ResponseEntity<VariantDto> {
        return ResponseEntity.ok(variantService.create(skillId, request))
    }

    @GetMapping("/api/variants/{id}")
    fun getById(@PathVariable id: Long): ResponseEntity<VariantDto> {
        return ResponseEntity.ok(variantService.getById(id))
    }

    @PutMapping("/api/variants/{id}")
    fun update(@PathVariable id: Long, @RequestBody request: CreateVariantRequest): ResponseEntity<VariantDto> {
        return ResponseEntity.ok(variantService.update(id, request))
    }

    @DeleteMapping("/api/variants/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        variantService.delete(id)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/api/variants/compare")
    fun compare(@RequestBody request: CompareRequest): ResponseEntity<CompareResultDto> {
        return ResponseEntity.ok(variantService.compare(request))
    }
}
