package org.idea.fithub.program.dto;

import lombok.Data;
import org.idea.fithub.routine.dto.RoutineResponseDto;

import java.time.LocalDate;
import java.util.List;

@Data
public class ProgramResponseDto {
    private Long id;
    private String name;
    private String description;
    private String learnerFirstName;
    private String trainerFirstName;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate createdAt;
    private List<RoutineResponseDto> routines;

    private Long learnerId;
    private Long trainerId;
}