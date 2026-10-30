package com.devkit.hsd.entity;

import com.devkit.hsd.common.BaseEntity;
import com.devkit.hsd.common.enums.ApprovalStatus;
import com.devkit.hsd.common.enums.EventTypes;
import com.devkit.hsd.common.enums.MeetingTypes;
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
        name = "meetings",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_meeting_identity_per_community", columnNames = {"community_id", "title", "date_time", "location"})
        },
        indexes = {
                @Index(name = "idx_meetings_community_id", columnList = "community_id"),
                @Index(name = "idx_meetings_community_date_time", columnList = "community_id, date_time")
        }
)
public class Meeting extends BaseEntity {

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

    @Enumerated(EnumType.STRING)
    @Column(name = "meeting_type", nullable = false, length = 50)
    private MeetingTypes meetingType;

    @Column(name = "agenda_items", columnDefinition = "TEXT")
    private String agendaItems;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "decisions", columnDefinition = "TEXT")
    private String decisions;

    @Column(name = "actions", columnDefinition = "TEXT")
    private String actions;

    @Column(name = "is_general", nullable = false)
    private Boolean isGeneral = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "committee_id")
    private Committee committee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id", nullable = false)
    private Community community;
}
