package com.devkit.hsd.entity;


import com.devkit.hsd.common.BaseEntity;
import com.devkit.hsd.common.enums.ApprovalStatus;
import com.devkit.hsd.common.enums.Priority;
import com.devkit.hsd.common.enums.ProjectStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(
        name = "projects",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_project_name_per_committee_tenant", columnNames = {"community_id", "committee_id", "name"})
        },
        indexes = {
                @Index(name = "idx_projects_community_id", columnList = "community_id"),
                @Index(name = "idx_projects_committee_community", columnList = "committee_id, community_id")
        }
)
public class Project extends BaseEntity {

    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    private ProjectStatus status = ProjectStatus.AKTIF;

    @Enumerated(EnumType.STRING)
    private ApprovalStatus approvalStatus = ApprovalStatus.PENDING;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Priority priority;

    @Column(name = "is_active")
    private Boolean isActive = Boolean.TRUE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "committee_id")
    private Committee committee;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "project_team_members",
    joinColumns = @JoinColumn(name = "project_id"),
    inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> teamMembers = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id", nullable = false)
    private Community community;
}
