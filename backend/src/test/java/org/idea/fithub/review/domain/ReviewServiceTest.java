package org.idea.fithub.review.domain;

import org.idea.fithub.exceptions.BadRequestException;
import org.idea.fithub.exceptions.DuplicateResourceException;
import org.idea.fithub.exceptions.InvalidOperationException;
import org.idea.fithub.exceptions.ResourceNotFoundException;
import org.idea.fithub.learner.domain.Learner;
import org.idea.fithub.program.domain.Program;
import org.idea.fithub.program.infrastructure.ProgramRepository;
import org.idea.fithub.review.dto.ReviewRequestDto;
import org.idea.fithub.review.dto.TrainerReviewResponseDto;
import org.idea.fithub.review.infrastructure.ReviewRepository;
import org.idea.fithub.trainer.domain.Trainer;
import org.idea.fithub.trainer.infrastructure.TrainerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private ProgramRepository programRepository;
    @Mock
    private TrainerRepository trainerRepository;
    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ReviewService reviewService;

    // --- Datos de Prueba Comunes ---
    private ReviewRequestDto testReviewRequestDto;
    private Program testProgram;
    private Trainer testTrainer;
    private Learner testLearner;
    private Review testReview;
    private Long programId = 1L;
    private Long trainerId = 10L;
    private Long learnerId = 100L;
    private Long reviewId = 1000L;
    private LocalDate pastDate;

    @BeforeEach
    void setUp() {
        /// Arrange global
        pastDate = LocalDate.now().minusDays(1);

        testLearner = new Learner();
        testLearner.setId(learnerId);

        testTrainer = new Trainer();
        testTrainer.setId(trainerId);

        testProgram = new Program();
        testProgram.setId(programId);
        testProgram.setTrainer(testTrainer);
        testProgram.setLearner(testLearner);
        testProgram.setEndDate(pastDate);

        testReviewRequestDto = new ReviewRequestDto();
        testReviewRequestDto.setProgramId(programId);
        testReviewRequestDto.setTargetId(trainerId);
        testReviewRequestDto.setRating(5);
        testReviewRequestDto.setComment("Excellent!");

        testReview = new Review();
        testReview.setId(reviewId);
        testReview.setProgram(testProgram);
        testReview.setAuthor(testLearner);
        testReview.setTarget(testTrainer);
        testReview.setRating(5);
        testReview.setComment("Excellent!");


        lenient().when(programRepository.findById(programId)).thenReturn(Optional.of(testProgram));
        lenient().when(programRepository.findById(99L)).thenReturn(Optional.empty());
        lenient().when(trainerRepository.findById(trainerId)).thenReturn(Optional.of(testTrainer));
        lenient().when(trainerRepository.findById(99L)).thenReturn(Optional.empty());
        lenient().when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(testReview));
        lenient().when(reviewRepository.findById(9999L)).thenReturn(Optional.empty());
        lenient().when(modelMapper.map(any(ReviewRequestDto.class), eq(Review.class))).thenReturn(new Review());
        lenient().when(reviewRepository.save(any(Review.class))).thenReturn(testReview);
    }

    // --- Tests para getReviewsByTrainer ---

    @Test
    void shouldReturnPagedReviewsWhenTrainerExists() {
        /// Arrange
        Long existingTrainerId = 10L;
        PageRequest pageRequest = PageRequest.of(0, 5);
        Page<TrainerReviewResponseDto> fakePage = new PageImpl<>(Collections.emptyList(), pageRequest, 0);

        when(trainerRepository.existsById(existingTrainerId)).thenReturn(true);
        when(reviewRepository.findByTargetId(existingTrainerId, pageRequest)).thenReturn(fakePage);

        /// Act
        Page<TrainerReviewResponseDto> resultPage = reviewService.getReviewsByTrainer(existingTrainerId, pageRequest);

        /// Assert
        assertThat(resultPage).isNotNull();
        assertThat(resultPage).isEqualTo(fakePage);
        verify(trainerRepository).existsById(existingTrainerId);
        verify(reviewRepository).findByTargetId(existingTrainerId, pageRequest);
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenGettingReviewsForNonExistentTrainer() {
        /// Arrange
        Long nonExistentTrainerId = 99L;
        PageRequest pageRequest = PageRequest.of(0, 5);
        when(trainerRepository.existsById(nonExistentTrainerId)).thenReturn(false);

        /// Act & Assert
        assertThatThrownBy(() -> reviewService.getReviewsByTrainer(nonExistentTrainerId, pageRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Trainer not found with id: " + nonExistentTrainerId);

        /// Verify
        verify(trainerRepository).existsById(nonExistentTrainerId);
        // No debe intentar buscar reviews si el trainer no existe
        verify(reviewRepository, never()).findByTargetId(anyLong(), any(Pageable.class));
    }

    // --- Tests para createReview ---

    @Test
    void shouldCreateReviewWhenAllValidationsPass() {
        /// Arrange (Happy Path)
        when(reviewRepository.existsByProgramId(programId)).thenReturn(false);
        when(modelMapper.map(testReviewRequestDto, Review.class)).thenReturn(testReview);
        when(reviewRepository.save(testReview)).thenReturn(testReview);

        /// Act
        Review result = reviewService.createReview(testReviewRequestDto);

        /// Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(reviewId);
        assertThat(result.getAuthor()).isEqualTo(testLearner);
        assertThat(result.getTarget()).isEqualTo(testTrainer);
        assertThat(result.getProgram()).isEqualTo(testProgram);

        /// Verify (Orden de las validaciones)
        verify(programRepository).findById(programId);
        verify(trainerRepository).findById(trainerId);
        verify(reviewRepository).existsByProgramId(programId);
        verify(modelMapper).map(testReviewRequestDto, Review.class);
        verify(reviewRepository).save(testReview);
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenProgramDoesNotExist() {
        /// Arrange
        testReviewRequestDto.setProgramId(99L);

        /// Act & Assert
        assertThatThrownBy(() -> reviewService.createReview(testReviewRequestDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Program not found");

        /// Verify
        verify(programRepository).findById(99L);
        verify(trainerRepository, never()).findById(anyLong());
        verify(reviewRepository, never()).existsByProgramId(anyLong());
        verify(reviewRepository, never()).save(any());
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenTargetTrainerDoesNotExist() {
        /// Arrange
        testReviewRequestDto.setTargetId(99L);

        /// Act & Assert
        assertThatThrownBy(() -> reviewService.createReview(testReviewRequestDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Trainer not found");

        /// Verify
        verify(programRepository).findById(programId);
        verify(trainerRepository).findById(99L);
        verify(reviewRepository, never()).save(any());
    }

    @Test
    void shouldThrowBadRequestExceptionWhenProgramHasNoTrainer() {
        /// Arrange
        testProgram.setTrainer(null);

        /// Act & Assert
        assertThatThrownBy(() -> reviewService.createReview(testReviewRequestDto))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Program does not have a trainer");

        /// Verify
        verify(programRepository).findById(programId);
        verify(trainerRepository).findById(trainerId);
        verify(reviewRepository, never()).save(any());
    }

    @Test
    void shouldThrowBadRequestExceptionWhenTargetTrainerMismatch() {
        /// Arrange
        Trainer wrongTrainer = new Trainer(); wrongTrainer.setId(99L);
        testReviewRequestDto.setTargetId(99L);
        when(trainerRepository.findById(99L)).thenReturn(Optional.of(wrongTrainer));

        /// Act & Assert
        assertThatThrownBy(() -> reviewService.createReview(testReviewRequestDto))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Target trainer does not match program trainer");

        /// Verify
        verify(programRepository).findById(programId);
        verify(trainerRepository).findById(99L);
        verify(reviewRepository, never()).save(any());
    }

    @Test
    void shouldThrowDuplicateResourceExceptionWhenReviewAlreadyExistsForProgram() {
        /// Arrange
        when(reviewRepository.existsByProgramId(programId)).thenReturn(true);

        /// Act & Assert
        assertThatThrownBy(() -> reviewService.createReview(testReviewRequestDto))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("A review already exists for this program");

        /// Verify
        verify(programRepository).findById(programId);
        verify(trainerRepository).findById(trainerId);
        verify(reviewRepository).existsByProgramId(programId);
        verify(reviewRepository, never()).save(any());
    }

    @Test
    void shouldThrowInvalidOperationExceptionWhenProgramHasNoEndDate() {
        /// Arrange
        testProgram.setEndDate(null);
        when(reviewRepository.existsByProgramId(programId)).thenReturn(false);

        /// Act & Assert
        assertThatThrownBy(() -> reviewService.createReview(testReviewRequestDto))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessageContaining("Cannot review a program without an end date");

        /// Verify
        verify(programRepository).findById(programId);
        verify(trainerRepository).findById(trainerId);
        verify(reviewRepository).existsByProgramId(programId);
        verify(reviewRepository, never()).save(any());
    }

    @Test
    void shouldThrowInvalidOperationExceptionWhenProgramHasNotEnded() {
        /// Arrange
        LocalDate futureDate = LocalDate.now().plusDays(1);
        testProgram.setEndDate(futureDate);
        when(reviewRepository.existsByProgramId(programId)).thenReturn(false);

        /// Act & Assert
        assertThatThrownBy(() -> reviewService.createReview(testReviewRequestDto))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessageContaining("Cannot review a program that has not ended yet");

        /// Verify
        verify(programRepository).findById(programId);
        verify(trainerRepository).findById(trainerId);
        verify(reviewRepository).existsByProgramId(programId);
        verify(reviewRepository, never()).save(any());
    }

    @Test
    void shouldThrowBadRequestExceptionWhenCommentIsBlank() {
        /// Arrange
        testReviewRequestDto.setComment("   ");
        when(reviewRepository.existsByProgramId(programId)).thenReturn(false);

        /// Act & Assert
        assertThatThrownBy(() -> reviewService.createReview(testReviewRequestDto))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Comment cannot be blank");

        /// Verify
        verify(programRepository).findById(programId);
        verify(trainerRepository).findById(trainerId);
        verify(reviewRepository).existsByProgramId(programId);
        assertThat(testProgram.getEndDate()).isNotNull();
        assertThat(testProgram.getEndDate()).isBeforeOrEqualTo(LocalDate.now());
        verify(modelMapper, never()).map(any(), any());
        verify(reviewRepository, never()).save(any());
    }

    // --- Tests para deleteReview ---

    @Test
    void shouldDeleteReviewWhenReviewExists() {
        /// Arrange

        /// Act
        reviewService.deleteReview(reviewId);

        /// Assert / Verify
        verify(reviewRepository).findById(reviewId);
        verify(reviewRepository).delete(testReview);
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenDeletingNonExistentReview() {
        /// Arrange
        Long nonExistentId = 9999L;

        /// Act & Assert
        assertThatThrownBy(() -> reviewService.deleteReview(nonExistentId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Review not found with id: " + nonExistentId);

        /// Verify
        verify(reviewRepository).findById(nonExistentId);
        verify(reviewRepository, never()).delete(any());
    }
}