package org.idea.fithub.learner.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.idea.fithub.trainer.domain.GoalType;

@Data
public class LearnerStatsDto {
    @NotNull
    private Double height;

    @NotNull
    private Double weight;

    @NotNull
    private GoalType goalType;
}
