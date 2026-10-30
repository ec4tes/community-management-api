package com.devkit.hsd.entity;

import com.devkit.hsd.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "communities")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Community extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(name = "university_name", nullable = false)
    private String universityName;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false,unique = true)
    private String slug;

    @Column(nullable = false)
    private Boolean active = true;

}
