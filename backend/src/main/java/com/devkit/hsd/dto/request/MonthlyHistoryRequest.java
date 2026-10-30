package com.devkit.hsd.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyHistoryRequest {

    @NotNull(message = "Year boş olamaz")
    @Min(value = 2000, message = "Year en az 2000 olmalıdır")
    @Max(value = 2100, message = "Year en fazla 2100 olabilir")
    private Integer year;

    @NotNull(message = "Month boş olamaz")
    @Min(value = 1, message = "Month en az 1 olmalıdır")
    @Max(value = 12, message = "Month en fazla 12 olabilir")
    private Integer month;
}