package com.skillsmp.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "skills")
class Skill(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    @JsonIgnore
    var project: Project = Project(),

    @Column(nullable = false)
    var name: String = "",

    var description: String? = null,

    @Column(name = "prompt_body")
    var promptBody: String? = null,

    @Lob
    var metadata: String = "{}",

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: EntityStatus = EntityStatus.DRAFT,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    @JsonIgnore
    var owner: User = User(),

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "skill_tags",
        joinColumns = [JoinColumn(name = "skill_id")],
        inverseJoinColumns = [JoinColumn(name = "tag_id")]
    )
    var tags: MutableSet<Tag> = mutableSetOf(),

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
)
