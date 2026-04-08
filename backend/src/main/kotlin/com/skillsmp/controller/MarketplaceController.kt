package com.skillsmp.controller

import com.skillsmp.dto.MarketplaceSkillDto
import com.skillsmp.service.MarketplaceService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/marketplace")
class MarketplaceController(
    private val marketplaceService: MarketplaceService
) {
    @GetMapping("/skills")
    fun getPublishedSkills(): ResponseEntity<List<MarketplaceSkillDto>> {
        return ResponseEntity.ok(marketplaceService.getPublishedSkills())
    }

    @GetMapping("/skills/{id}")
    fun getSkillById(@PathVariable id: Long): ResponseEntity<MarketplaceSkillDto> {
        return ResponseEntity.ok(marketplaceService.getSkillById(id))
    }
}
