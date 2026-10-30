package com.devkit.hsd.dto.request;

import com.devkit.hsd.common.enums.ApprovalStatus;
import com.devkit.hsd.common.enums.EventTypes;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CreateEventRequest {

    @NotBlank(message = "Etkinlik başlığı boş olamaz")
    @Size(max = 150, message = "Etkinlik başlığı en fazla 150 karakter olabilir")
    private String title;

    @Size(max = 5000, message = "Etkinlik açıklaması en fazla 5000 karakter olabilir")
    private String description;

    @NotNull(message = "Etkinlik türü boş olamaz")
    private EventTypes eventType;

    @NotNull(message = "Etkinlik tarihi boş olamaz")
    @Future(message = "Etkinlik tarihi gelecekte bir zaman olmalıdır")
    private LocalDateTime dateTime;

    @NotBlank(message = "Etkinlik konumu boş olamaz")
    @Size(max = 200, message = "Etkinlik konumu en fazla 200 karakter olabilir")
    private String location;

    @Size(max = 500, message = "Poster görsel yolu en fazla 500 karakter olabilir")
    private String posterImage;

    @NotNull(message = "Katılım puanı boş olamaz")
    @Min(value = 0, message = "Katılım puanı 0'dan küçük olamaz")
    @Max(value = 1000, message = "Katılım puanı en fazla 1000 olabilir")
    private Integer attendancePoints;

    @NotNull(message = "Onay durumu boş olamaz")
    private ApprovalStatus approvalStatus;

    private Long communityId;
}
