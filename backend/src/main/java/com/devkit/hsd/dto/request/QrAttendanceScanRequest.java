package com.devkit.hsd.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QrAttendanceScanRequest {

    @NotBlank(message = "QR kod verisi boş olamaz")
    private String code;
}
