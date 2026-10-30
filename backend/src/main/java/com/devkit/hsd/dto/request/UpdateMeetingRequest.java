package com.devkit.hsd.dto.request;

import java.time.LocalDateTime;

import com.devkit.hsd.common.enums.ApprovalStatus;
import com.devkit.hsd.common.enums.EventTypes;
import com.devkit.hsd.common.enums.MeetingTypes;

import jakarta.validation.constraints.Future;
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
public class UpdateMeetingRequest {


    @Size(max = 150, message = "Toplantı başlığı en fazla 150 karakter olabilir")
    private String title;

    @Size(max = 5000, message = "Toplantı açıklaması en fazla 5000 karakter olabilir")
    private String description;


    private EventTypes eventType;


    @Future(message = "Toplantı tarihi gelecekte bir zaman olmalıdır")
    private LocalDateTime dateTime;

    @Size(max = 200, message = "Toplantı konumu en fazla 200 karakter olabilir")
    private String location;

    @Size(max = 500, message = "Poster görsel yolu en fazla 500 karakter olabilir")
    private String posterImage;


    @Min(value = 0, message = "Katılım puanı 0'dan küçük olamaz")
    @Max(value = 1000, message = "Katılım puanı en fazla 1000 olabilir")
    private Integer attendancePoints;


    private ApprovalStatus approvalStatus;


    private MeetingTypes meetingType;

    @Size(max = 5000, message = "Gündem maddeleri en fazla 5000 karakter olabilir")
    private String agendaItems;

    @Size(max = 5000, message = "Notlar en fazla 5000 karakter olabilir")
    private String notes;

    @Size(max = 5000, message = "Kararlar en fazla 5000 karakter olabilir")
    private String decisions;

    @Size(max = 5000, message = "Aksiyonlar en fazla 5000 karakter olabilir")
    private String actions;


    private Boolean isGeneral;

    private Long committeeId;
}


