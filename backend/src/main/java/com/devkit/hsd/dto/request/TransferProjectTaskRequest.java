package com.devkit.hsd.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransferProjectTaskRequest {

        @NotNull(message = "Hedef kullanıcı id boş olamaz")
        private Long targetUserId;

}
