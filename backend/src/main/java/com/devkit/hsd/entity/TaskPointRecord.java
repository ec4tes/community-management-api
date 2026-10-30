package com.devkit.hsd.entity;

import com.devkit.hsd.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(
        name = "task_point_record",
        indexes = {
                @Index(name = "idx_task_point_record_community_created_at", columnList = "community_id, created_at"),
                @Index(name = "idx_task_point_record_user_created_at", columnList = "user_id, created_at")
        }
)
public class TaskPointRecord extends BaseEntity {

    private Integer points;

    private String description;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne
    @JoinColumn(name = "project_task_id", nullable = false)
    private ProjectTask projectTask;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id", nullable = false)
    private Community community;
}
