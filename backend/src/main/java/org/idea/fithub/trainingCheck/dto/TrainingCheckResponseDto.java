package org.idea.fithub.trainingCheck.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
public class TrainingCheckResponseDto {
    private Long learnerId;
    private Integer totalChecks;
    private String duration;
    private List<LocalDate> checkDates;
}
