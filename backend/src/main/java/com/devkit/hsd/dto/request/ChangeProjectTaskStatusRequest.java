package com.devkit.hsd.dto.request;

import com.devkit.hsd.common.enums.TaskStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChangeProjectTaskStatusRequest {


    @NotNull(message = "Görev durumu boş olamaz!")
    private TaskStatus status;

}

