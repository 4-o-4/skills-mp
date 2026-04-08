package com.skillsmp.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "environment_deployments")
class EnvironmentDeployment(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "environment_id", nullable = false)
    @JsonIgnore
    var environment: Environment = Environment(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "version_id", nullable = false)
    @JsonIgnore
    var version: Version = Version(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deployed_by_id", nullable = false)
    @JsonIgnore
    var deployedBy: User = User(),

    @Column(name = "deployed_at", nullable = false)
    var deployedAt: LocalDateTime = LocalDateTime.now()
)
