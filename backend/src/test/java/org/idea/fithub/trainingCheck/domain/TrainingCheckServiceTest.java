package org.idea.fithub.trainingCheck.domain;

import org.idea.fithub.exceptions.BadRequestException;
import org.idea.fithub.exceptions.DuplicateResourceException;
import org.idea.fithub.exceptions.ResourceNotFoundException;
import org.idea.fithub.learner.domain.Learner;
import org.idea.fithub.learner.infrastructure.LearnerRepository;
import org.idea.fithub.trainingCheck.dto.TrainingCheckResponseDto;
import org.idea.fithub.trainingCheck.infrastructure.TrainingCheckRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class TrainingCheckServiceTest {

    // --- Mocks (Dependencias Falsas) ---
    @Mock
    private TrainingCheckRepository trainingCheckRepository;
    @Mock
    private LearnerRepository learnerRepository;

    // --- InjectMocks (La clase real bajo prueba) ---
    @InjectMocks
    private TrainingCheckService trainingCheckService;

    // --- Datos de Prueba Comunes ---
    private Learner testLearner;
    private Long learnerId = 1L;
    private LocalDate today;
    private LocalDate validPastDate;

    @BeforeEach
    void setUp() {
        /// Arrange global
        testLearner = new Learner();
        testLearner.setId(learnerId);

        today = LocalDate.now();
        validPastDate = today.minusDays(5);

        lenient().when(learnerRepository.findById(learnerId)).thenReturn(Optional.of(testLearner));
    }

    // --- Tests para markCheck ---

    @Test
    void shouldSaveCheckWhenLearnerExistsAndDateIsValidAndNotDuplicate() {
        /// Arrange (Happy Path)

        when(trainingCheckRepository.existsByLearnerAndDate(testLearner, validPastDate)).thenReturn(false);

        /// Act
        trainingCheckService.markCheck(learnerId, validPastDate);

        /// Assert / Verify
        verify(trainingCheckRepository, times(1)).save(any(TrainingCheck.class));
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenMarkingCheckForNonExistentLearner() {
        /// Arrange
        Long nonExistentId = 99L;
        when(learnerRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        /// Act & Assert
        assertThatThrownBy(() -> trainingCheckService.markCheck(nonExistentId, today))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Learner not found with id: " + nonExistentId);

        /// Verify
        verify(trainingCheckRepository, never()).existsByLearnerAndDate(any(), any());
        verify(trainingCheckRepository, never()).save(any());
    }

    @Test
    void shouldThrowBadRequestExceptionWhenMarkingCheckWithNullDate() {
        /// Arrange
        LocalDate nullDate = null;

        /// Act & Assert
        assertThatThrownBy(() -> trainingCheckService.markCheck(learnerId, nullDate))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Date cannot be null");

        /// Verify
        verify(trainingCheckRepository, never()).save(any());
    }

    @Test
    void shouldThrowBadRequestExceptionWhenMarkingCheckForFutureDate() {
        /// Arrange
        LocalDate futureDate = today.plusDays(1);

        /// Act & Assert
        assertThatThrownBy(() -> trainingCheckService.markCheck(learnerId, futureDate))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Cannot mark attendance for a future date");

        /// Verify
        verify(trainingCheckRepository, never()).save(any());
    }

    @Test
    void shouldThrowBadRequestExceptionWhenMarkingCheckForTooOldDate() {
        /// Arrange
        LocalDate tooOldDate = today.minusYears(1).minusDays(1); // Más de 1 año

        /// Act & Assert
        assertThatThrownBy(() -> trainingCheckService.markCheck(learnerId, tooOldDate))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Cannot mark attendance for dates older than 1 year");

        /// Verify
        verify(trainingCheckRepository, never()).save(any());
    }

    @Test
    void shouldThrowDuplicateResourceExceptionWhenMarkingCheckThatAlreadyExists() {
        /// Arrange
        when(trainingCheckRepository.existsByLearnerAndDate(testLearner, validPastDate)).thenReturn(true);

        /// Act & Assert
        assertThatThrownBy(() -> trainingCheckService.markCheck(learnerId, validPastDate))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Learner has already marked attendance for this day");

        /// Verify
        verify(trainingCheckRepository, never()).save(any());
    }

    // --- Tests para unmarkCheck ---

    @Test
    void shouldDeleteCheckWhenLearnerAndCheckExist() {
        /// Arrange (Happy Path)
        TrainingCheck existingCheck = new TrainingCheck(validPastDate, testLearner);
        when(trainingCheckRepository.findByLearnerAndDate(testLearner, validPastDate))
                .thenReturn(Optional.of(existingCheck));

        /// Act
        trainingCheckService.unmarkCheck(learnerId, validPastDate);

        /// Assert / Verify
        verify(trainingCheckRepository, times(1)).delete(existingCheck);
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenUnmarkingCheckForNonExistentLearner() {
        /// Arrange
        Long nonExistentId = 99L;
        when(learnerRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        /// Act & Assert
        assertThatThrownBy(() -> trainingCheckService.unmarkCheck(nonExistentId, validPastDate))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Learner not found with id: " + nonExistentId);

        /// Verify
        verify(trainingCheckRepository, never()).findByLearnerAndDate(any(), any());
        verify(trainingCheckRepository, never()).delete(any());
    }

    @Test
    void shouldThrowBadRequestExceptionWhenUnmarkingCheckWithNullDate() {
        /// Arrange
        LocalDate nullDate = null;

        /// Act & Assert
        assertThatThrownBy(() -> trainingCheckService.unmarkCheck(learnerId, nullDate))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Date cannot be null");

        /// Verify
        verify(trainingCheckRepository, never()).delete(any());
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenUnmarkingCheckThatDoesNotExist() {
        /// Arrange
        when(trainingCheckRepository.findByLearnerAndDate(testLearner, validPastDate))
                .thenReturn(Optional.empty());

        /// Act & Assert
        assertThatThrownBy(() -> trainingCheckService.unmarkCheck(learnerId, validPastDate))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("There is no register for that day");

        /// Verify
        verify(trainingCheckRepository, never()).delete(any());
    }

    // --- Tests para getTrainingCheck ---

    @Test
    void shouldReturnCorrectDtoWhenLearnerExists() {
        /// Arrange (Happy Path)
        TrainingCheck check1 = new TrainingCheck(today.minusDays(2), testLearner);
        TrainingCheck check2 = new TrainingCheck(today, testLearner);
        List<TrainingCheck> checks = List.of(check1, check2);

        when(trainingCheckRepository.findByLearner(testLearner)).thenReturn(checks);

        /// Act
        TrainingCheckResponseDto responseDto = trainingCheckService.getTrainingCheck(learnerId);

        /// Assert
        assertThat(responseDto).isNotNull();
        assertThat(responseDto.getLearnerId()).isEqualTo(learnerId);
        assertThat(responseDto.getTotalChecks()).isEqualTo(2);
        assertThat(responseDto.getCheckDates()).containsExactly(today.minusDays(2), today);
    }

    @Test
    void shouldReturnDtoWithEmptyListWhenLearnerHasNoChecks() {
        /// Arrange
        when(trainingCheckRepository.findByLearner(testLearner)).thenReturn(Collections.emptyList());

        /// Act
        TrainingCheckResponseDto responseDto = trainingCheckService.getTrainingCheck(learnerId);

        /// Assert
        assertThat(responseDto).isNotNull();
        assertThat(responseDto.getLearnerId()).isEqualTo(learnerId);
        assertThat(responseDto.getTotalChecks()).isZero();
        assertThat(responseDto.getCheckDates()).isEmpty();
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenGettingChecksForNonExistentLearner() {
        /// Arrange
        Long nonExistentId = 99L;
        when(learnerRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        /// Act & Assert
        assertThatThrownBy(() -> trainingCheckService.getTrainingCheck(nonExistentId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Learner not found with id: " + nonExistentId);

        /// Verify
        verify(trainingCheckRepository, never()).findByLearner(any());
    }
}