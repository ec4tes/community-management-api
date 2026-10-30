package com.devkit.hsd.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserRequest {

    @Size(min = 3, max = 50, message = "Username 3 ile 50 karakter arasında olmalıdır")
    private String username;

    @Email(message = "Email formatı geçersiz")
    @Size(max = 100, message = "Email en fazla 100 karakter olabilir")
    private String email;

    @Size(max = 50, message = "Ad en fazla 50 karakter olabilir")
    private String firstName;

    @Size(max = 50, message = "Soyad en fazla 50 karakter olabilir")
    private String lastName;

    @Size(max = 255, message = "Profil görseli en fazla 255 karakter olabilir")
    private String profileImage;

    @Size(max = 100, message = "Bölüm en fazla 100 karakter olabilir")
    private String department;

    @Min(value = 1, message = "Sınıf en az 1 olmalıdır")
    @Max(value = 6, message = "Sınıf en fazla 6 olabilir")
    private Integer grade;
}
