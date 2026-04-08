package com.skillsmp.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "versions")
class Version(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_id", nullable = false)
    @JsonIgnore
    var skill: Skill = Skill(),

    @Column(name = "version_number", nullable = false)
    var versionNumber: String = "",

    @Column(nullable = false)
    var content: String = "",

    var changelog: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: EntityStatus = EntityStatus.DRAFT,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    @JsonIgnore
    var author: User = User(),

    @Column(name = "git_commit_sha")
    var gitCommitSha: String? = null,

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()
)
