package org.idea.fithub.review.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReviewResponseDto {

    private Long id;
    private Integer rating;
    private String comment;

    private Long authorId;
    private Long targetId;
    private Long programId;
}