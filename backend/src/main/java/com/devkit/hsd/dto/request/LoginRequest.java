package com.devkit.hsd.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {

    @NotBlank(message = "Kullanıcı adı boş bırakılamaz.")
    private String username;

    @NotBlank(message = "Şifre boş bırakılamaz.")
    private String password;
}
