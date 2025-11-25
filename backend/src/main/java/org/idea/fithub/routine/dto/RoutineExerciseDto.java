package org.idea.fithub.routine.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.idea.fithub.exercise.dto.ExerciseRequestDto;

@Data
@NoArgsConstructor
public class RoutineExerciseDto {
    @Valid
    @NotNull
    private ExerciseRequestDto exerciseRequestDto;

    @NotNull
    private Integer sets;

    @NotNull
    private Integer repetition;

    private Double weight;
}
