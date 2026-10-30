package com.devkit.hsd.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MeetingAttendanceScanRequest {

    @NotNull(message = "Toplantı id boş olamaz")
    private Long meetingId;

    @NotBlank(message = "QR kod verisi boş olamaz")
    private String qrCodeData;
}