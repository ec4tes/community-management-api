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
public class AddSkillRequest {

    @NotBlank(message = "Yetenek adı boş olamaz")
    private String name;

   

}
