package org.idea.fithub.review.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ReviewRequestDto {
    @Min(value = 0)
    @Max(value = 5)
    @NotNull
    private Integer rating;

    @NotNull
    private Long targetId;

    @NotNull
    private Long programId;

    @NotNull
    @Size(max = 255)
    private String comment;
}
