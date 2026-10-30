package com.devkit.hsd.entity;

import com.devkit.hsd.common.BaseEntity;
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
        name = "meeting_attendance",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_meeting_user_attendance", columnNames = {"meeting_id", "user_id"})
        },
        indexes = {
                @Index(name = "idx_meeting_attendance_community_id", columnList = "community_id")
        }
)
public class MeetingAttendance extends BaseEntity {

    @Column(name = "scanned_at", nullable = false)
    private LocalDateTime scannedAt;

    @Column(name = "points_earned", nullable = false)
    private Integer pointsEarned;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "meeting_id", nullable = false)
    private Meeting meeting;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id", nullable = false)
    private Community community;
}
