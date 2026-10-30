package com.devkit.hsd.entity;

import com.devkit.hsd.common.BaseEntity;
import com.devkit.hsd.common.enums.Priority;
import com.devkit.hsd.common.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "project_task",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_project_task_title_per_project_tenant", columnNames = {"community_id", "project_id", "title"})
        },
        indexes = {
                @Index(name = "idx_project_task_community_id", columnList = "community_id"),
                @Index(name = "idx_project_task_project_community", columnList = "project_id, community_id"),
                @Index(name = "idx_project_task_community_status", columnList = "community_id, status")
        }
)
public class ProjectTask extends BaseEntity {


    private String title;

    private String description;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TaskStatus status = TaskStatus.MUSAIT;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Priority priority;

    @Column(nullable = false)
    private Integer points;

    @Column(nullable = false)
    private LocalDateTime deadline;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to")
    private User assignedTo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id", nullable = false)
    private Community community;

}
