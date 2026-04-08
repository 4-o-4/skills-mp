package com.skillsmp.controller

import com.skillsmp.dto.CreateTagRequest
import com.skillsmp.dto.TagDto
import com.skillsmp.service.TagService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/tags")
class TagController(
    private val tagService: TagService
) {
    @GetMapping
    fun getAll(): ResponseEntity<List<TagDto>> {
        return ResponseEntity.ok(tagService.getAll())
    }

    @PostMapping
    fun create(@RequestBody request: CreateTagRequest): ResponseEntity<TagDto> {
        return ResponseEntity.ok(tagService.create(request))
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        tagService.delete(id)
        return ResponseEntity.noContent().build()
    }
}
