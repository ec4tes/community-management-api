package com.devkit.hsd.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyHistoryResponse {

    private String month;
    private Integer totalPoints;
    private Long completedTaskCount;
    private Long completedEventCount;
}