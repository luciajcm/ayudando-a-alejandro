package org.idea.fithub.exercise.domain;

import org.idea.fithub.exceptions.BadRequestException;
import org.idea.fithub.exceptions.ConflictException;
import org.idea.fithub.exceptions.DuplicateResourceException;
import org.idea.fithub.exceptions.ResourceNotFoundException;
import org.idea.fithub.exercise.dto.ExerciseRequestDto;
import org.idea.fithub.exercise.dto.ExerciseResponseDto;
import org.idea.fithub.exercise.infrastructure.ExerciseRepository;
import org.idea.fithub.routineExercise.domain.RoutineExercise;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;


import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExerciseServiceTest {

    @Mock
    private ExerciseRepository exerciseRepository;
    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ExerciseService exerciseService;

    private Exercise testExercise;
    private ExerciseRequestDto testExerciseRequestDto;
    private ExerciseResponseDto testExerciseResponseDto;
    private Long exerciseId = 1L;
    private String existingName = "Bench Press";
    private String newName = "Incline Bench Press";
    private Muscle existingMuscle = Muscle.CHEST;
    private Muscle newMuscle = Muscle.SHOULDERS;
    private String existingAsset = "chest_press.gif";
    private String newAsset = "incline_press.gif";


    @BeforeEach
    void setUp() {
        /// Arrange global
        testExercise = new Exercise();
        testExercise.setId(exerciseId);
        testExercise.setName(existingName);
        testExercise.setMuscle(existingMuscle);
        testExercise.setAsset(existingAsset);
        testExercise.setRoutines(new ArrayList<>());

        testExerciseRequestDto = new ExerciseRequestDto();
        testExerciseRequestDto.setName(newName);
        testExerciseRequestDto.setMuscle(newMuscle);
        testExerciseRequestDto.setAsset(newAsset);

        testExerciseResponseDto = new ExerciseResponseDto();
        testExerciseResponseDto.setId(exerciseId);
        testExerciseResponseDto.setName(existingName);
        testExerciseResponseDto.setMuscle(existingMuscle);

        // Mockeos lenient
        lenient().when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.of(testExercise));
        lenient().when(exerciseRepository.findById(99L)).thenReturn(Optional.empty()); // No existente
        lenient().when(modelMapper.map(any(Exercise.class), eq(ExerciseResponseDto.class))).thenReturn(testExerciseResponseDto);
        lenient().when(modelMapper.map(any(ExerciseRequestDto.class), eq(Exercise.class))).thenReturn(new Exercise());
        lenient().when(exerciseRepository.save(any(Exercise.class))).thenReturn(testExercise); // save devuelve la entidad
    }

    @Test
    void shouldReturnExerciseDtoWhenExerciseExists() {
        /// Arrange

        /// Act
        ExerciseResponseDto result = exerciseService.getExercise(exerciseId);

        /// Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(exerciseId);
        verify(exerciseRepository).findById(exerciseId);
        verify(modelMapper).map(testExercise, ExerciseResponseDto.class);
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenGettingNonExistentExercise() {
        /// Arrange
        Long nonExistentId = 99L;

        /// Act & Assert
        assertThatThrownBy(() -> exerciseService.getExercise(nonExistentId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Exercise not found with id: " + nonExistentId);
        verify(exerciseRepository).findById(nonExistentId);
        verify(modelMapper, never()).map(any(), any());
    }


    @Test
    void shouldSaveExerciseWhenNameNotDuplicate() {
        /// Arrange (Happy Path)
        Exercise mappedExercise = new Exercise();
        when(exerciseRepository.findByName(testExerciseRequestDto.getName())).thenReturn(Optional.empty());
        when(modelMapper.map(testExerciseRequestDto, Exercise.class)).thenReturn(mappedExercise);
        when(exerciseRepository.save(mappedExercise)).thenReturn(mappedExercise);

        /// Act
        Exercise result = exerciseService.createExercise(testExerciseRequestDto);

        /// Assert
        assertThat(result).isNotNull();
        verify(exerciseRepository).findByName(testExerciseRequestDto.getName());
        verify(modelMapper).map(testExerciseRequestDto, Exercise.class);
        verify(exerciseRepository).save(mappedExercise);
    }

    @Test
    void shouldThrowDuplicateResourceExceptionWhenCreatingExerciseWithExistingName() {
        /// Arrange
        when(exerciseRepository.findByName(testExerciseRequestDto.getName())).thenReturn(Optional.of(new Exercise()));

        /// Act & Assert
        assertThatThrownBy(() -> exerciseService.createExercise(testExerciseRequestDto))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Exercise already exists with name: " + testExerciseRequestDto.getName());

        /// Verify
        verify(exerciseRepository).findByName(testExerciseRequestDto.getName());
        verify(modelMapper, never()).map(any(), any());
        verify(exerciseRepository, never()).save(any());
    }

    @Test
    void shouldDeleteExerciseWhenExistsAndNotInRoutines() {
        /// Arrange

        /// Act
        exerciseService.deleteExercise(exerciseId);

        /// Assert / Verify
        verify(exerciseRepository).findById(exerciseId);
        assertThat(testExercise.getRoutines()).isEmpty();
        verify(exerciseRepository).delete(testExercise);
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenDeletingNonExistentExercise() {
        /// Arrange
        Long nonExistentId = 99L;

        /// Act & Assert
        assertThatThrownBy(() -> exerciseService.deleteExercise(nonExistentId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Exercise not found with id: " + nonExistentId);

        /// Verify
        verify(exerciseRepository).findById(nonExistentId);
        verify(exerciseRepository, never()).delete(any());
    }

    @Test
    void shouldThrowConflictExceptionWhenDeletingExerciseInUse() {
        /// Arrange
        testExercise.getRoutines().add(new RoutineExercise());

        /// Act & Assert
        assertThatThrownBy(() -> exerciseService.deleteExercise(exerciseId))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Cannot delete exercise. It is being used in");

        /// Verify
        verify(exerciseRepository).findById(exerciseId);
        verify(exerciseRepository, never()).delete(any());
    }

    // --- Tests para updateExercise (Muscle) ---

    @Test
    void shouldUpdateMuscleWhenExerciseExistsAndMuscleIsValid() {
        /// Arrange
        Muscle muscleToUpdate = Muscle.SHOULDERS;

        /// Act
        ExerciseResponseDto result = exerciseService.updateExercise(exerciseId, muscleToUpdate);

        /// Assert
        assertThat(result).isNotNull();
        assertThat(testExercise.getMuscle()).isEqualTo(muscleToUpdate);
        verify(exerciseRepository).findById(exerciseId);
        verify(exerciseRepository).save(testExercise);
        verify(modelMapper).map(testExercise, ExerciseResponseDto.class);
    }

    @Test
    void shouldThrowResourceNotFoundWhenUpdatingMuscleForNonExistentExercise() {
        /// Arrange
        Long nonExistentId = 99L;

        /// Act & Assert
        assertThatThrownBy(() -> exerciseService.updateExercise(nonExistentId, Muscle.LEGS))
                .isInstanceOf(ResourceNotFoundException.class);

        /// Verify
        verify(exerciseRepository).findById(nonExistentId);
        verify(exerciseRepository, never()).save(any());
    }

    @Test
    void shouldUpdateAssetWhenExerciseExistsAndAssetIsValid() {
        /// Arrange
        String assetToUpdate = "new_asset.gif";

        /// Act
        ExerciseResponseDto result = exerciseService.updateExercise(exerciseId, assetToUpdate);

        /// Assert
        assertThat(result).isNotNull();
        assertThat(testExercise.getAsset()).isEqualTo(assetToUpdate);
        verify(exerciseRepository).findById(exerciseId);
        verify(exerciseRepository).save(testExercise);
        verify(modelMapper).map(testExercise, ExerciseResponseDto.class);
    }

    @Test
    void shouldThrowBadRequestExceptionWhenUpdatingAssetWithNull() {
        /// Arrange
        String nullAsset = null;

        /// Act & Assert
        assertThatThrownBy(() -> exerciseService.updateExercise(exerciseId, nullAsset))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Exercise asset is required.");
        verify(exerciseRepository, never()).save(any());
    }

    @Test
    void shouldThrowBadRequestExceptionWhenUpdatingAssetWithBlank() {
        /// Arrange
        String blankAsset = "   ";

        /// Act & Assert
        assertThatThrownBy(() -> exerciseService.updateExercise(exerciseId, blankAsset))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Exercise asset is required.");
        verify(exerciseRepository, never()).save(any());
    }

    @Test
    void shouldUpdateExerciseWhenDtoIsValidAndNameNotChanged() {
        /// Arrange
        testExerciseRequestDto.setName(existingName);

        /// Act
        ExerciseResponseDto result = exerciseService.updateExercise(exerciseId, testExerciseRequestDto);

        /// Assert
        assertThat(result).isNotNull();
        verify(exerciseRepository).findById(exerciseId);
        verify(exerciseRepository, never()).findByName(anyString());
        verify(modelMapper).map(testExerciseRequestDto, testExercise);
        verify(exerciseRepository).save(testExercise);
    }

    @Test
    void shouldUpdateExerciseWhenDtoIsValidAndNameChangedAndNotDuplicate() {
        /// Arrange
        testExerciseRequestDto.setName(newName);
        when(exerciseRepository.findByName(newName)).thenReturn(Optional.empty());

        /// Act
        ExerciseResponseDto result = exerciseService.updateExercise(exerciseId, testExerciseRequestDto);

        /// Assert
        assertThat(result).isNotNull();
        verify(exerciseRepository).findById(exerciseId);
        verify(exerciseRepository).findByName(newName);
        verify(modelMapper).map(testExerciseRequestDto, testExercise);
        verify(exerciseRepository).save(testExercise);
        verify(modelMapper).map(testExercise, ExerciseResponseDto.class);
    }

    @Test
    void shouldThrowDuplicateResourceExceptionWhenUpdatingExerciseNameToExistingOne() {
        /// Arrange
        String duplicateName = "Existing Other Exercise";
        testExerciseRequestDto.setName(duplicateName);
        when(exerciseRepository.findByName(duplicateName)).thenReturn(Optional.of(new Exercise()));

        /// Act & Assert
        assertThatThrownBy(() -> exerciseService.updateExercise(exerciseId, testExerciseRequestDto))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Exercise already exists with name: " + duplicateName);

        /// Verify
        verify(exerciseRepository).findById(exerciseId);
        verify(exerciseRepository).findByName(duplicateName);
        verify(modelMapper, never()).map(eq(testExerciseRequestDto), any(Exercise.class));
        verify(exerciseRepository, never()).save(any());
    }
}