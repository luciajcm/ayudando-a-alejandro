package org.idea.fithub.review.application;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.idea.fithub.review.domain.ReviewService;
import org.idea.fithub.review.dto.ReviewRequestDto;
import org.idea.fithub.review.dto.ReviewResponseDto;
import org.idea.fithub.review.dto.TrainerReviewResponseDto;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewController {
    private final ReviewService reviewService;
    private final ModelMapper modelMapper;

    @GetMapping("/{trainerId}")
    @PreAuthorize("hasAnyRole('ADMIN','TRAINER','LEARNER')")
    public ResponseEntity<Page<TrainerReviewResponseDto>> getReviewsByTrainer(
            @PathVariable Long trainerId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return ResponseEntity.ok(
                reviewService.getReviewsByTrainer(trainerId, PageRequest.of(page, size)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','LEARNER')")
    public ResponseEntity<ReviewResponseDto> createReview(@Valid @RequestBody
                                               ReviewRequestDto reviewRequestDto) {
        var review = reviewService.createReview(reviewRequestDto);
        var location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(review.getId())
                .toUri();

        var responseDto = modelMapper.map(review, ReviewResponseDto.class);

        return ResponseEntity.created(location).body(responseDto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','LEARNER')")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);

        return ResponseEntity.noContent().build();
    }
}
