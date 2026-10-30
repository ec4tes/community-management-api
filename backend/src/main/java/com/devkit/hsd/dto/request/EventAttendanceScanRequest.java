package com.devkit.hsd.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventAttendanceScanRequest {

    @NotNull(message = "Etkinlik id boş olamaz")
    private Long eventId;

    @NotBlank(message = "QR kod verisi boş olamaz")
    private String qrCodeData;
}