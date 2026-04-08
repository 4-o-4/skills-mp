package com.skillsmp.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "git_configs")
class GitConfig(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", unique = true, nullable = false)
    @JsonIgnore
    var project: Project = Project(),

    @Column(name = "repo_url", nullable = false)
    var repoUrl: String = "",

    var branch: String = "main",

    var token: String? = null,

    @Column(nullable = false)
    var connected: Boolean = false,

    @Column(name = "last_sync_at")
    var lastSyncAt: LocalDateTime? = null,

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()
)
