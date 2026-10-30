package com.devkit.hsd.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddSocialLinkRequest {

    @NotBlank(message = "Platform adı boş olamaz")
    private String platform;

    @NotBlank(message = "URL boş olamaz")
    private String url;

}
