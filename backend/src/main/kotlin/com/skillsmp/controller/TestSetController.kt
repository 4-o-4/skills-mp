package com.skillsmp.controller

import com.skillsmp.dto.*
import com.skillsmp.service.TestSetService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/test-sets")
class TestSetController(
    private val testSetService: TestSetService
) {
    @GetMapping
    fun getByProjectId(@RequestParam projectId: Long): ResponseEntity<List<TestSetDto>> {
        return ResponseEntity.ok(testSetService.getByProjectId(projectId))
    }

    @PostMapping
    fun create(@RequestBody request: CreateTestSetRequest): ResponseEntity<TestSetDto> {
        return ResponseEntity.ok(testSetService.create(request))
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ResponseEntity<TestSetDetailDto> {
        return ResponseEntity.ok(testSetService.getById(id))
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody request: UpdateTestSetRequest): ResponseEntity<TestSetDto> {
        return ResponseEntity.ok(testSetService.update(id, request))
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        testSetService.delete(id)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/{id}/cases")
    fun addCase(@PathVariable id: Long, @RequestBody request: CreateTestCaseRequest): ResponseEntity<TestCaseDto> {
        return ResponseEntity.ok(testSetService.addCase(id, request))
    }
}
