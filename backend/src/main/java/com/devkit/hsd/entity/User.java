package com.devkit.hsd.entity;

import com.devkit.hsd.common.BaseEntity;
import com.devkit.hsd.common.enums.Role;
import com.devkit.hsd.common.enums.SystemRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User extends BaseEntity implements UserDetails {

    @NotNull
    @Column(unique = true, nullable = false)
    private String username;

    @NotNull
    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "total_points", nullable = false,columnDefinition = "integer default 0")
    private Integer totalPoints = 0;

    @NotNull
    @Column(nullable = false)
    private String password;


    @Column(nullable = false)
    @NotNull
    private String firstName;

    @Column(nullable = false)
    @NotNull
    private String lastName;

    private String profileImage;

    @NotNull
    private String department;

    @NotNull
    private Integer grade;


    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SystemRole systemRole = SystemRole.USER;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role = Role.UYE;

    @Column(nullable = false)
    private Boolean isActive = true;

    @Column(nullable = false)
    private Boolean isOnline = false;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Skill> skills = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SocialLink> socialLinks = new ArrayList<>();

    @ManyToMany(mappedBy = "members")
    private List<Committee> committees = new ArrayList<>();

    private LocalDateTime lastSeen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id")
    private Community community;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + systemRole.name()));
        if (role != null) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.name()));
        }
        return authorities;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public boolean isEnabled() {
        return Boolean.TRUE.equals(this.isActive);
    }
}