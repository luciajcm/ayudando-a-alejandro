package org.idea.fithub.exercise.dto;

import lombok.Data;
import org.idea.fithub.exercise.domain.Muscle;

@Data
public class ExerciseResponseDto {
    private Long id;
    private String name;
    private String asset;
    private String description;
    private Muscle muscle;
}