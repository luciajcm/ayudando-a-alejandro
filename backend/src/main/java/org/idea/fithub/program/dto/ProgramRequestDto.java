package org.idea.fithub.program.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class ProgramRequestDto {
    private String name;

    @Size(max = 255)
    private String description;

    @NotNull
    private Long learnerId;

    private Long trainerId;

    private LocalDate startDate;

    private LocalDate endDate;

    private List<Long> routineIds;
}
