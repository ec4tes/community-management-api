package com.devkit.hsd.entity;

import com.devkit.hsd.common.BaseEntity;
import com.devkit.hsd.common.enums.SkillProficiency;
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
@Table(name = "skill")
public class Skill extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SkillProficiency proficiency;
}
