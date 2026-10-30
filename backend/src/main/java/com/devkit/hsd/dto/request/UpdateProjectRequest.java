package com.devkit.hsd.dto.request;

import com.devkit.hsd.common.enums.ApprovalStatus;
import com.devkit.hsd.common.enums.Priority;
import com.devkit.hsd.common.enums.ProjectStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProjectRequest {

    @NotBlank(message = "Proje ismi boş bırakılamaz.")
    private String name;

    @NotBlank(message = "Proje açıklaması boş bırakılamaz.")
    private String description;

    @NotNull(message = "Öncelik seviyesi seçilmelidir.")
    private Priority priority;

    @NotNull(message = "Komite ID boş bırakılamaz.")
    private Long committeeId;

    private ProjectStatus status;
    private ApprovalStatus approvalStatus;
    private Boolean isActivity;
}
