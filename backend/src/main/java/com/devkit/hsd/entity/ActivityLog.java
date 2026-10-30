package com.devkit.hsd.entity;

import com.devkit.hsd.common.BaseEntity;
import com.devkit.hsd.common.enums.ActivityLogActionType;
import com.devkit.hsd.common.enums.ActivityLogSource;
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
        name = "activity_log",
        indexes = {
                @Index(name = "idx_activity_log_community_created_at", columnList = "community_id, created_at"),
                @Index(name = "idx_activity_log_user_community_created_at", columnList = "user_id, community_id, created_at")
        }
)
public class ActivityLog extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false)
    private ActivityLogActionType actionType;

    @Column(length = 500)
    private String description;

    @Column(name = "points")
    private Integer points;

    @Enumerated(EnumType.STRING)
    @Column(name = "source", nullable = false)
    private ActivityLogSource source;

    @Column(name = "source_id", nullable = false)
    private Long sourceId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne
    @JoinColumn(name = "project_task_id")
    private ProjectTask projectTask;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id", nullable = false)
    private Community community;
}
