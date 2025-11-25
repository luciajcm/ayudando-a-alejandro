package org.idea.fithub.exercise.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.idea.fithub.exercise.domain.Muscle;

@Data
public class ExerciseRequestDto {
    @Size(max = 100)
    @NotNull
    private String name;

    @Size(max = 255)
    private String asset;

    @Size(max = 255)
    private String description;

    @NotNull
    private Muscle muscle;
}
