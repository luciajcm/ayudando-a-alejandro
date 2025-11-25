package org.idea.fithub.routine.domain;

import org.idea.fithub.exceptions.BadRequestException;
import org.idea.fithub.exceptions.DuplicateResourceException;
import org.idea.fithub.exceptions.InvalidOperationException;
import org.idea.fithub.exceptions.ResourceNotFoundException;
import org.idea.fithub.exercise.domain.Exercise;
import org.idea.fithub.exercise.domain.Muscle;
import org.idea.fithub.exercise.dto.ExerciseRequestDto;
import org.idea.fithub.exercise.infrastructure.ExerciseRepository;
import org.idea.fithub.program.domain.Program;
import org.idea.fithub.routine.dto.RoutineExerciseDto;
import org.idea.fithub.routine.dto.RoutineRequestDto;
import org.idea.fithub.routine.dto.RoutineResponseDto;
import org.idea.fithub.routine.infrastructure.RoutineRepository;
import org.idea.fithub.routineExercise.domain.RoutineExercise;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoutineServiceTest {

    @Mock
    private RoutineRepository routineRepository;
    @Mock
    private ExerciseRepository exerciseRepository;
    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private RoutineService routineService;

    private Routine testRoutine;
    private Exercise testExercise;
    private RoutineRequestDto testRoutineRequestDto;
    private RoutineResponseDto testRoutineResponseDto;
    private RoutineExerciseDto testRoutineExerciseDto;
    private ExerciseRequestDto testExerciseRequestDto;

    private Long routineId = 1L;
    private Long exerciseId = 10L;
    private String routineName = "Leg Day";
    private Day routineDay = Day.MONDAY;

    @BeforeEach
    void setUp() {
        /// Arrange global
        testRoutine = new Routine();
        testRoutine.setId(routineId);
        testRoutine.setName(routineName);
        testRoutine.setDay(routineDay);
        testRoutine.setExercises(new ArrayList<>());
        testRoutine.setPrograms(new ArrayList<>());

        testExercise = new Exercise();
        testExercise.setId(exerciseId);
        testExercise.setName("Squat");
        testExercise.setMuscle(Muscle.LEGS);

        testRoutineRequestDto = new RoutineRequestDto();
        testRoutineRequestDto.setName(routineName);
        testRoutineRequestDto.setDay(routineDay);

        testRoutineResponseDto = new RoutineResponseDto();
        testRoutineResponseDto.setId(routineId);
        testRoutineResponseDto.setName(routineName);
        testRoutineResponseDto.setDay(routineDay);

        testExerciseRequestDto = new ExerciseRequestDto();
        testExerciseRequestDto.setName("Squat");
        testExerciseRequestDto.setMuscle(Muscle.LEGS);

        testRoutineExerciseDto = new RoutineExerciseDto();
        testRoutineExerciseDto.setExerciseRequestDto(testExerciseRequestDto);
        testRoutineExerciseDto.setSets(3);
        testRoutineExerciseDto.setRepetition(10);
        testRoutineExerciseDto.setWeight(100.0);

        lenient().when(routineRepository.findById(routineId)).thenReturn(Optional.of(testRoutine));
        lenient().when(routineRepository.findById(99L)).thenReturn(Optional.empty());
        lenient().when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.of(testExercise));
        lenient().when(exerciseRepository.findById(99L)).thenReturn(Optional.empty());
        lenient().when(modelMapper.map(any(Routine.class), eq(RoutineResponseDto.class))).thenReturn(testRoutineResponseDto);
        lenient().when(modelMapper.map(any(RoutineRequestDto.class), eq(Routine.class))).thenReturn(new Routine());
    }

    // --- Tests para createRoutine ---

    @Test
    void shouldSaveRoutineWhenDataIsValidAndNotDuplicate() {
        /// Arrange (Happy Path)
        Routine mappedRoutine = new Routine();
        when(routineRepository.existsByNameAndDay(routineName, routineDay)).thenReturn(false);
        when(modelMapper.map(testRoutineRequestDto, Routine.class)).thenReturn(mappedRoutine);
        when(routineRepository.save(mappedRoutine)).thenReturn(mappedRoutine);

        /// Act
        Routine result = routineService.createRoutine(testRoutineRequestDto);

        /// Assert
        assertThat(result).isNotNull();
        verify(routineRepository).existsByNameAndDay(routineName, routineDay);
        verify(modelMapper).map(testRoutineRequestDto, Routine.class);
        verify(routineRepository).save(mappedRoutine);
    }

    @Test
    void shouldThrowBadRequestExceptionWhenCreatingRoutineWithNullName() {
        /// Arrange
        testRoutineRequestDto.setName(null);

        /// Act & Assert
        assertThatThrownBy(() -> routineService.createRoutine(testRoutineRequestDto))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Routine name is required");
        verify(routineRepository, never()).save(any());
    }

    @Test
    void shouldThrowBadRequestExceptionWhenCreatingRoutineWithNullDay() {
        /// Arrange
        testRoutineRequestDto.setDay(null);

        /// Act & Assert
        assertThatThrownBy(() -> routineService.createRoutine(testRoutineRequestDto))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Routine day is required");
        verify(routineRepository, never()).save(any());
    }

    @Test
    void shouldThrowDuplicateResourceExceptionWhenCreatingRoutineWithExistingNameAndDay() {
        /// Arrange
        when(routineRepository.existsByNameAndDay(routineName, routineDay)).thenReturn(true);

        /// Act & Assert
        assertThatThrownBy(() -> routineService.createRoutine(testRoutineRequestDto))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("already exists");

        /// Verify
        verify(routineRepository).existsByNameAndDay(routineName, routineDay);
        verify(modelMapper, never()).map(any(), any());
        verify(routineRepository, never()).save(any());
    }

    // --- Tests para updateRoutine ---

    @Test
    void shouldUpdateRoutineWhenDataIsValidAndNotDuplicateOnOthers() {
        /// Arrange (Happy Path)
        RoutineRequestDto updateDto = new RoutineRequestDto();
        updateDto.setName("Updated Leg Day");
        updateDto.setDay(Day.FRIDAY);

        when(routineRepository.findByNameAndDay(updateDto.getName(), updateDto.getDay())).thenReturn(Optional.empty());
        when(routineRepository.save(testRoutine)).thenReturn(testRoutine);

        /// Act
        RoutineResponseDto result = routineService.updateRoutine(routineId, updateDto);

        /// Assert
        assertThat(result).isNotNull();
        verify(routineRepository).findById(routineId);
        verify(routineRepository).findByNameAndDay(updateDto.getName(), updateDto.getDay());
        verify(modelMapper).map(updateDto, testRoutine);
        verify(routineRepository).save(testRoutine);
        verify(modelMapper).map(testRoutine, RoutineResponseDto.class);
    }

    @Test
    void shouldNotThrowDuplicateWhenUpdatingRoutineToSameNameAndDay() {
        /// Arrange
        RoutineRequestDto updateDto = new RoutineRequestDto();
        updateDto.setName(routineName);
        updateDto.setDay(routineDay);

        when(routineRepository.findByNameAndDay(updateDto.getName(), updateDto.getDay()))
                .thenReturn(Optional.of(testRoutine));
        when(routineRepository.save(testRoutine)).thenReturn(testRoutine);

        /// Act
        RoutineResponseDto result = routineService.updateRoutine(routineId, updateDto);

        /// Assert
        assertThat(result).isNotNull();
        verify(routineRepository).findByNameAndDay(updateDto.getName(), updateDto.getDay());
        verify(modelMapper).map(updateDto, testRoutine);
        verify(routineRepository).save(testRoutine);
    }

    @Test
    void shouldThrowDuplicateResourceExceptionWhenUpdatingRoutineToExistingNameAndDayOfAnother() {
        /// Arrange
        RoutineRequestDto updateDto = new RoutineRequestDto();
        updateDto.setName("Existing Routine");
        updateDto.setDay(Day.TUESDAY);

        Routine anotherRoutine = new Routine();
        anotherRoutine.setId(2L);
        anotherRoutine.setName("Existing Routine");
        anotherRoutine.setDay(Day.TUESDAY);

        when(routineRepository.findByNameAndDay(updateDto.getName(), updateDto.getDay()))
                .thenReturn(Optional.of(anotherRoutine));

        /// Act & Assert
        assertThatThrownBy(() -> routineService.updateRoutine(routineId, updateDto))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("already exists");

        /// Verify
        verify(routineRepository).findById(routineId);
        verify(routineRepository).findByNameAndDay(updateDto.getName(), updateDto.getDay());
        verify(modelMapper, never()).map(eq(updateDto), any(Routine.class));
        verify(routineRepository, never()).save(any());
    }

    // --- Tests para deleteRoutine ---

    @Test
    void shouldDeleteRoutineWhenExistsAndHasNoPrograms() {
        /// Arrange
        testRoutine.setPrograms(new ArrayList<>());

        /// Act
        routineService.deleteRoutine(routineId);

        /// Assert / Verify
        verify(routineRepository).findById(routineId);
        verify(routineRepository).delete(testRoutine);
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenDeletingNonExistentRoutine() {
        /// Arrange
        Long nonExistentId = 99L;

        /// Act & Assert
        assertThatThrownBy(() -> routineService.deleteRoutine(nonExistentId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Routine not found with id: " + nonExistentId);

        /// Verify
        verify(routineRepository).findById(nonExistentId);
        verify(routineRepository, never()).delete(any());
    }

    @Test
    void shouldThrowInvalidOperationExceptionWhenDeletingRoutineWithPrograms() {
        /// Arrange
        testRoutine.setPrograms(List.of(new Program()));

        /// Act & Assert
        assertThatThrownBy(() -> routineService.deleteRoutine(routineId))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessageContaining("Cannot delete routine. It is assigned to");

        /// Verify
        verify(routineRepository).findById(routineId);
        verify(routineRepository, never()).delete(any());
    }

    // --- Tests para addExercise ---

    @Test
    void shouldAddExistingExerciseToRoutineWhenValid() {
        /// Arrange (Happy Path con ejercicio existente)
        when(exerciseRepository.findByName("Squat")).thenReturn(Optional.of(testExercise));
        when(routineRepository.save(testRoutine)).thenReturn(testRoutine);

        /// Act
        Routine result = routineService.addExercise(routineId, testRoutineExerciseDto);

        /// Assert
        assertThat(result).isNotNull();
        assertThat(testRoutine.getExercises()).hasSize(1);
        assertThat(testRoutine.getExercises().get(0).getExercise()).isEqualTo(testExercise);
        assertThat(testRoutine.getExercises().get(0).getSets()).isEqualTo(3);

        /// Verify
        verify(routineRepository).findById(routineId);
        verify(exerciseRepository).findByName("Squat");
        verify(exerciseRepository, never()).save(any(Exercise.class));
        verify(routineRepository).save(testRoutine);
    }

    @Test
    void shouldCreateAndAddExerciseToRoutineWhenExerciseDoesNotExist() {
        /// Arrange (Happy Path con ejercicio nuevo)
        Exercise newExercise = new Exercise();
        newExercise.setId(11L);
        newExercise.setName("New Exercise");

        testRoutineExerciseDto.getExerciseRequestDto().setName("New Exercise");
        when(exerciseRepository.findByName("New Exercise")).thenReturn(Optional.empty());
        when(modelMapper.map(testRoutineExerciseDto.getExerciseRequestDto(), Exercise.class)).thenReturn(newExercise);
        when(exerciseRepository.save(newExercise)).thenReturn(newExercise);
        when(routineRepository.save(testRoutine)).thenReturn(testRoutine);

        /// Act
        Routine result = routineService.addExercise(routineId, testRoutineExerciseDto);

        /// Assert
        assertThat(result).isNotNull();
        assertThat(testRoutine.getExercises()).hasSize(1);
        assertThat(testRoutine.getExercises().get(0).getExercise()).isEqualTo(newExercise);

        /// Verify
        verify(routineRepository).findById(routineId);
        verify(exerciseRepository).findByName("New Exercise");
        verify(modelMapper).map(testRoutineExerciseDto.getExerciseRequestDto(), Exercise.class);
        verify(exerciseRepository).save(newExercise);
        verify(routineRepository).save(testRoutine);
    }

    @Test
    void shouldThrowDuplicateResourceExceptionWhenAddingExerciseAlreadyInRoutine() {
        /// Arrange
        RoutineExercise existingRoutineExercise = new RoutineExercise(testRoutine, testExercise, 3, 10, 100.0);
        testRoutine.getExercises().add(existingRoutineExercise);

        when(exerciseRepository.findByName("Squat")).thenReturn(Optional.of(testExercise));

        /// Act & Assert
        assertThatThrownBy(() -> routineService.addExercise(routineId, testRoutineExerciseDto))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("already in this routine");

        /// Verify
        verify(routineRepository).findById(routineId);
        verify(exerciseRepository).findByName("Squat");
        verify(routineRepository, never()).save(any());
    }

    // --- Tests para deleteExercise ---

    @Test
    void shouldRemoveExerciseFromRoutineWhenExistsInRoutine() {
        /// Arrange (Happy Path)
        RoutineExercise routineExercise = new RoutineExercise(testRoutine, testExercise, 3, 10, 100.0);
        testRoutine.getExercises().add(routineExercise);

        /// Act
        routineService.deleteExercise(routineId, exerciseId);

        /// Assert
        assertThat(testRoutine.getExercises()).isEmpty();

        /// Verify
        verify(routineRepository).findById(routineId);
        verify(exerciseRepository).findById(exerciseId);
        verify(routineRepository).save(testRoutine);
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenDeletingExerciseNotInRoutine() {
        /// Arrange

        /// Act & Assert
        assertThatThrownBy(() -> routineService.deleteExercise(routineId, exerciseId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Exercise not found in this routine");

        /// Verify
        verify(routineRepository).findById(routineId);
        verify(exerciseRepository).findById(exerciseId);
        verify(routineRepository, never()).save(any());
    }

    // --- Tests para getRoutineExercises --- (Simplificado)

    @Test
    void shouldReturnCorrectExercisesPageWhenRoutineExists() {
        /// Arrange
        Exercise exercise2 = new Exercise(); exercise2.setId(11L);
        RoutineExercise re1 = new RoutineExercise(testRoutine, testExercise, 3, 10, 100.0);
        RoutineExercise re2 = new RoutineExercise(testRoutine, exercise2, 4, 8, 50.0);
        testRoutine.setExercises(List.of(re1, re2));

        RoutineExerciseDto dto1 = new RoutineExerciseDto();
        RoutineExerciseDto dto2 = new RoutineExerciseDto();
        when(modelMapper.map(re1, RoutineExerciseDto.class)).thenReturn(dto1);
        lenient().when(modelMapper.map(re2, RoutineExerciseDto.class)).thenReturn(dto2);

        PageRequest pageRequest = PageRequest.of(0, 1);

        /// Act
        List<RoutineExerciseDto> result = routineService.getRoutineExercises(routineId, pageRequest);

        /// Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(dto1);

        /// Verify
        verify(routineRepository).findById(routineId);
        verify(modelMapper).map(re1, RoutineExerciseDto.class);
        verify(modelMapper, never()).map(re2, RoutineExerciseDto.class);
    }
}