package org.idea.fithub.trainer.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TrainerExperienceDto {
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate experienceStartDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate experienceEndDate;
}
