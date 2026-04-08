package com.skillsmp.controller

import com.skillsmp.dto.CreateTestRunRequest
import com.skillsmp.dto.TestRunDetailDto
import com.skillsmp.dto.TestRunDto
import com.skillsmp.service.TestRunService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.security.Principal

@RestController
@RequestMapping("/api/test-runs")
class TestRunController(
    private val testRunService: TestRunService
) {
    @GetMapping
    fun getByVersionId(@RequestParam(required = false) versionId: Long?): ResponseEntity<List<TestRunDto>> {
        if (versionId != null) {
            return ResponseEntity.ok(testRunService.getByVersionId(versionId))
        }
        return ResponseEntity.ok(emptyList())
    }

    @PostMapping
    fun create(@RequestBody request: CreateTestRunRequest, principal: Principal): ResponseEntity<TestRunDto> {
        return ResponseEntity.ok(testRunService.create(request, principal.name))
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ResponseEntity<TestRunDetailDto> {
        return ResponseEntity.ok(testRunService.getById(id))
    }

    @PostMapping("/{id}/cancel")
    fun cancel(@PathVariable id: Long): ResponseEntity<TestRunDto> {
        return ResponseEntity.ok(testRunService.cancel(id))
    }
}
