package org.idea.fithub.routine.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.idea.fithub.routine.domain.Day;

import java.util.List;

@Data
public class RoutineRequestDto {
    @NotNull
    private Day day;

    @Size(max = 100)
    private String name;

    private List<Long> exerciseIds;
}
