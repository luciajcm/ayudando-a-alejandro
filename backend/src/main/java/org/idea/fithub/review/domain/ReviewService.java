package org.idea.fithub.review.domain;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.idea.fithub.exceptions.BadRequestException;
import org.idea.fithub.exceptions.DuplicateResourceException;
import org.idea.fithub.exceptions.InvalidOperationException;
import org.idea.fithub.exceptions.ResourceNotFoundException;
import org.idea.fithub.program.infrastructure.ProgramRepository;
import org.idea.fithub.review.dto.ReviewRequestDto;
import org.idea.fithub.review.dto.TrainerReviewResponseDto;
import org.idea.fithub.review.infrastructure.ReviewRepository;
import org.idea.fithub.trainer.infrastructure.TrainerRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ProgramRepository programRepository;
    private final TrainerRepository trainerRepository;
    private final ModelMapper modelMapper;

    public Page<TrainerReviewResponseDto> getReviewsByTrainer(Long trainerId, PageRequest pageRequest) {
        if (!trainerRepository.existsById(trainerId))
            throw new ResourceNotFoundException("Trainer not found with id: " + trainerId);

        return reviewRepository.findByTargetId(trainerId, pageRequest);
    }

    @Transactional
    public Review createReview(ReviewRequestDto reviewRequestDto) {
        var program = programRepository.findById(reviewRequestDto.getProgramId()).
                orElseThrow(() -> new ResourceNotFoundException(
                        "Program not found with id: " + reviewRequestDto.getProgramId()));

        var trainer = trainerRepository.findById(reviewRequestDto.getTargetId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Trainer not found with id: " + reviewRequestDto.getTargetId()));

        if (program.getTrainer() == null) {
            throw new BadRequestException("Program does not have a trainer");
        }

        if (!program.getTrainer().getId().equals(reviewRequestDto.getTargetId())) {
            throw new BadRequestException(
                    "Target trainer does not match program trainer. " +
                            "Expected trainer ID: " + program.getTrainer().getId());
        }

        if (reviewRepository.existsByProgramId(program.getId())) {
            throw new DuplicateResourceException(
                    "A review already exists for this program");
        }

        if (program.getEndDate() == null) {
            throw new InvalidOperationException(
                    "Cannot review a program without an end date");
        }

        if (program.getEndDate().isAfter(LocalDate.now())) {
            throw new InvalidOperationException(
                    "Cannot review a program that has not ended yet. " +
                    "Program ends on: " + program.getEndDate());
        }

        if (reviewRequestDto.getComment().isBlank())
            throw new BadRequestException("Comment cannot be blank");

        var review = modelMapper.map(reviewRequestDto, Review.class);

        review.setProgram(program);
        review.setAuthor(program.getLearner());
        review.setTarget(trainer);

        return reviewRepository.save(review);
    }

    @Transactional
    public void deleteReview(Long id) {
        var review = reviewRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + id));

        reviewRepository.delete(review);
    }
}
