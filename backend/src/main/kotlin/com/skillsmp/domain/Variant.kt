package com.skillsmp.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "variants")
class Variant(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_id", nullable = false)
    @JsonIgnore
    var skill: Skill = Skill(),

    @Column(nullable = false)
    var name: String = "",

    @Column(name = "model_provider")
    var modelProvider: String? = null,

    @Column(name = "model_name")
    var modelName: String? = null,

    @Column(name = "system_prompt")
    var systemPrompt: String? = null,

    var temperature: Double = 0.7,

    @Column(name = "max_tokens")
    var maxTokens: Int = 1024,

    @Lob
    var config: String = "{}",

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()
)
