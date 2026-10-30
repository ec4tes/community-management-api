package com.devkit.hsd.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCommunityRequest {
    @NotBlank(message = "Topluluk adı boş bırakılamaz")
    private String name;

    @NotBlank(message = "Üniversite adı boş bırakılamaz")
    private String universityName;

    @NotBlank(message = "Şehir boş bırakılamaz")
    private String city;

    @NotBlank(message = "Topluluk slug değeri boş bırakılamaz")
    private String slug;

    @NotNull(message = "Topluluk yöneticisi (adminUserId) boş bırakılamaz")
    private Long adminUserId;
}