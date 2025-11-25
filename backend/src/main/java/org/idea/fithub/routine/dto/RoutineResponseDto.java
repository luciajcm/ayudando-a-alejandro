package org.idea.fithub.routine.dto;

import lombok.Data;
import org.idea.fithub.exercise.dto.ExerciseResponseDto;
import org.idea.fithub.routine.domain.Day;

import java.util.List;

@Data
public class RoutineResponseDto {
    private Long id;
    private String name;
    private String description;
    private Day day;
    private List<ExerciseResponseDto> exercises;
}
