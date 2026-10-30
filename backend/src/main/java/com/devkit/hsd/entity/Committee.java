package com.devkit.hsd.entity;

import com.devkit.hsd.common.BaseEntity;
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
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "committees",
        indexes = {
                @Index(name = "idx_committees_community_id", columnList = "community_id")
        }
)
public class Committee extends BaseEntity {

    private String name;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leader_id")
    private User leader;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id", nullable = false)
    private Community community;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vice_leader_id")
    private User viceLeader;

    @ManyToMany
    @JoinTable(
            name = "committee_members",
            joinColumns = @JoinColumn(name = "committee_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> members = new ArrayList<>();

}
