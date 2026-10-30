package com.devkit.hsd.entity;

import com.devkit.hsd.common.BaseEntity;
import com.devkit.hsd.common.enums.ApprovalStatus;
import com.devkit.hsd.common.enums.EventTypes;
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
        name = "event",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_event_identity_per_community", columnNames = {"community_id", "title", "date_time", "location"})
        },
        indexes = {
                @Index(name = "idx_event_community_id", columnList = "community_id"),
                @Index(name = "idx_event_community_date_time", columnList = "community_id, date_time")
        }
)
public class Event extends BaseEntity {

    @Column(name = "title", nullable = false, length = 150)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 50)
    private EventTypes eventType;

    @Column(name = "date_time", nullable = false)
    private LocalDateTime dateTime;

    @Column(name = "location", nullable = false, length = 200)
    private String location;

    @Column(name = "poster_image", length = 500)
    private String posterImage;

    @Column(name = "qr_code_data", nullable = false, unique = true, length = 255)
    private String qrCodeData;

    @Column(name = "qr_expires_at", nullable = false)
    private LocalDateTime qrExpiresAt;

    @Column(name = "attendance_points", nullable = false)
    private Integer attendancePoints;

    @Enumerated(EnumType.STRING)
    @Column(name = "approval_status", nullable = false, length = 50)
    private ApprovalStatus approvalStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id", nullable = false)
    private Community community;
}
