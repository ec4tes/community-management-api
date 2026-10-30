package com.devkit.hsd.entity;

import com.devkit.hsd.common.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "social_link")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SocialLink extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    private String platformName;

    private String url;
}
