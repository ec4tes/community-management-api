package com.devkit.hsd.entity;

import com.devkit.hsd.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "refresh_token")
public class RefreshToken extends BaseEntity {

    @Column(name = "refresh_token")
    private String refreshToken;

    private LocalDateTime expiredDate;

    @ManyToOne
    private User user;
}
