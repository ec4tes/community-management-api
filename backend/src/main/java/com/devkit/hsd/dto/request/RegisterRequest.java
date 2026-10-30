package com.devkit.hsd.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Username boş bırakılamaz.")
    private String username;

    @NotBlank(message = "Email boş bırakılamaz.")
    @Email(message = "Düzgün gir")
    private String email;

    @NotBlank(message = "Şifre boş bırakılamaz.")
    @Size(min = 6, max = 20, message = "Şifre 6 ile 20 karakter arasında olmalıdır.")
    private String password;

    @NotBlank(message = "İsim boş bırakılamaz.")
    private String firstName;

    @NotBlank(message = "Soyisim boş bırakılamaz.")
    private String lastName;

    @NotBlank(message = "Departman boş bırakılamaz.")
    private String department;

    @NotNull(message = "Grade boş bırakılamaz.")
    private Integer grade;

    @NotNull(message = "communityId boş bırakılamaz.")
    private Long communityId;
}
