package com.devkit.hsd.dto.request;

import com.devkit.hsd.common.enums.Priority;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateProjectTaskRequest {

    @NotBlank(message = "Görev başlığı boş bırakılamaz.")
    private String title;

    @NotBlank(message = "Görev açıklaması boş bırakılamaz.")
    private String description;

    @NotNull(message = "Puan alanı boş olamaz")
    @Min(value = 0, message = "Puan 0'dan küçük olamaz")
    private Integer points;

    @NotNull(message = "Bitiş tarihi belirtilmelidir.")
    private LocalDateTime deadline;

    @NotNull(message = "Öncelik seviyesi seçilmelidir.")
    private Priority priority;

    @NotNull(message = "Proje ID boş bırakılamaz.")
    private Long projectId;

}
