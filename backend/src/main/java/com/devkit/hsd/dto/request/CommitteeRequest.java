package com.devkit.hsd.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommitteeRequest {

    @NotBlank(message = "Komite adı boş bırakılamaz.")
    private String name;

    @NotBlank(message = "Komite açıklaması boş bırakılamaz.")
    private String description;

    @NotNull(message = "Lider ID boş bırakılamaz.")
    private Long leaderId;

    @NotNull(message = "Başkan yardımcısı ID boş bırakılamaz.")
    private Long viceLeaderId;

    @NotEmpty(message = "Komite üye listesi boş olamaz.")
    private List<Long> membersIds;

    @NotNull(message = "Topluluk ID boş bırakılamaz.")
    private Long communityId;
}