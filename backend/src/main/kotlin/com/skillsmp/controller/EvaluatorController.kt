package com.skillsmp.controller

import com.skillsmp.dto.CreateEvaluatorRequest
import com.skillsmp.dto.EvaluatorDto
import com.skillsmp.dto.UpdateEvaluatorRequest
import com.skillsmp.service.EvaluatorService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/evaluators")
class EvaluatorController(
    private val evaluatorService: EvaluatorService
) {
    @GetMapping
    fun getByProjectId(@RequestParam projectId: Long): ResponseEntity<List<EvaluatorDto>> {
        return ResponseEntity.ok(evaluatorService.getByProjectId(projectId))
    }

    @PostMapping
    fun create(@RequestBody request: CreateEvaluatorRequest): ResponseEntity<EvaluatorDto> {
        return ResponseEntity.ok(evaluatorService.create(request))
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ResponseEntity<EvaluatorDto> {
        return ResponseEntity.ok(evaluatorService.getById(id))
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody request: UpdateEvaluatorRequest): ResponseEntity<EvaluatorDto> {
        return ResponseEntity.ok(evaluatorService.update(id, request))
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        evaluatorService.delete(id)
        return ResponseEntity.noContent().build()
    }
}
