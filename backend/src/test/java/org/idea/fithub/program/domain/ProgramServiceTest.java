package org.idea.fithub.program.domain;

import org.idea.fithub.exceptions.BadRequestException;
import org.idea.fithub.exceptions.DuplicateResourceException;
import org.idea.fithub.exceptions.InvalidOperationException;
import org.idea.fithub.exceptions.ResourceNotFoundException;
import org.idea.fithub.learner.domain.Learner;
import org.idea.fithub.learner.infrastructure.LearnerRepository;
import org.idea.fithub.program.dto.ProgramRequestDto;
import org.idea.fithub.program.dto.ProgramResponseDto;
import org.idea.fithub.program.infrastructure.ProgramRepository;
import org.idea.fithub.routine.domain.Routine;
import org.idea.fithub.routine.infrastructure.RoutineRepository;
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


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProgramServiceTest {

    @Mock
    private ProgramRepository programRepository;
    @Mock
    private LearnerRepository learnerRepository;
    @Mock
    private TrainerRepository trainerRepository;
    @Mock
    private RoutineRepository routineRepository;
    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ProgramService programService;

    // --- Datos de Prueba Comunes ---
    private ProgramRequestDto testProgramRequestDto;
    private Program testProgram;
    private ProgramResponseDto testProgramResponseDto;
    private Learner testLearner;
    private Trainer testTrainer;
    private Routine testRoutine;
    private Long programId = 1L;
    private Long learnerId = 10L;
    private Long trainerId = 100L;
    private Long routineId = 1000L;
    private LocalDate today;
    private LocalDate futureDate;
    private LocalDate pastDate;

    @BeforeEach
    void setUp() {
        /// Arrange global
        today = LocalDate.now();
        futureDate = today.plusDays(5);
        pastDate = today.minusDays(5);

        testLearner = new Learner();
        testLearner.setId(learnerId);
        testLearner.setPrograms(new ArrayList<>());

        testTrainer = new Trainer();
        testTrainer.setId(trainerId);

        testRoutine = new Routine();
        testRoutine.setId(routineId);

        testProgram = new Program();
        testProgram.setId(programId);
        testProgram.setLearner(testLearner);
        testProgram.setTrainer(testTrainer);
        testProgram.setProgramRoutines(new ArrayList<>());

        testProgramRequestDto = new ProgramRequestDto();
        testProgramRequestDto.setLearnerId(learnerId);
        testProgramRequestDto.setTrainerId(trainerId);
        testProgramRequestDto.setName("Valid Program Name");
        testProgramRequestDto.setStartDate(today);
        testProgramRequestDto.setEndDate(futureDate);

        testProgramResponseDto = new ProgramResponseDto();
        testProgramResponseDto.setId(programId);

        lenient().when(learnerRepository.findById(learnerId)).thenReturn(Optional.of(testLearner));
        lenient().when(learnerRepository.findById(99L)).thenReturn(Optional.empty());
        lenient().when(trainerRepository.findById(trainerId)).thenReturn(Optional.of(testTrainer));
        lenient().when(trainerRepository.findById(99L)).thenReturn(Optional.empty());
        lenient().when(programRepository.findById(programId)).thenReturn(Optional.of(testProgram));
        lenient().when(programRepository.findById(99L)).thenReturn(Optional.empty());
        lenient().when(routineRepository.findById(routineId)).thenReturn(Optional.of(testRoutine));
        lenient().when(routineRepository.findById(99L)).thenReturn(Optional.empty());
        lenient().when(modelMapper.map(any(ProgramRequestDto.class), eq(Program.class))).thenReturn(new Program());
        lenient().when(modelMapper.map(any(Program.class), eq(ProgramResponseDto.class))).thenReturn(testProgramResponseDto);
        lenient().when(programRepository.save(any(Program.class))).thenReturn(testProgram);
    }

    // --- Tests para createProgram ---

    @Test
    void shouldCreateProgramWhenDataIsValid() {
        /// Arrange (Happy Path)
        Program mappedProgram = new Program();
        when(modelMapper.map(testProgramRequestDto, Program.class)).thenReturn(mappedProgram);

        /// Act
        ProgramResponseDto result = programService.createProgram(testProgramRequestDto);

        /// Assert
        assertThat(result).isNotNull();
        assertThat(mappedProgram.getLearner()).isEqualTo(testLearner);
        assertThat(mappedProgram.getTrainer()).isEqualTo(testTrainer);
        assertThat(mappedProgram.getCreatedAt()).isEqualTo(today);

        /// Verify Orden
        verify(learnerRepository).findById(learnerId);
        verify(trainerRepository).findById(trainerId);
        verify(modelMapper).map(testProgramRequestDto, Program.class);
        verify(programRepository).save(mappedProgram);
        verify(modelMapper).map(testProgram, ProgramResponseDto.class);
    }

    @Test
    void shouldCreateProgramWhenNameIsNull() {
        /// Arrange
        testProgramRequestDto.setName(null);
        Program mappedProgram = new Program();
        when(modelMapper.map(testProgramRequestDto, Program.class)).thenReturn(mappedProgram);

        /// Act
        ProgramResponseDto result = programService.createProgram(testProgramRequestDto);

        /// Assert
        assertThat(result).isNotNull();
        assertThat(testLearner.getPrograms()).isEmpty();
        verify(programRepository).save(mappedProgram);
    }

    @Test
    void shouldThrowBadRequestExceptionWhenLearnerIdIsNull() {
        /// Arrange
        testProgramRequestDto.setLearnerId(null);

        /// Act & Assert
        assertThatThrownBy(() -> programService.createProgram(testProgramRequestDto))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Learner id is required");
        verify(programRepository, never()).save(any());
    }

    @Test
    void shouldThrowBadRequestExceptionWhenTrainerIdIsNull() {
        /// Arrange
        testProgramRequestDto.setTrainerId(null);

        /// Act & Assert
        assertThatThrownBy(() -> programService.createProgram(testProgramRequestDto))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Trainer id is required");
        verify(programRepository, never()).save(any());
    }

    @Test
    void shouldThrowBadRequestExceptionWhenEndDateIsBeforeStartDate() {
        /// Arrange
        testProgramRequestDto.setStartDate(today);
        testProgramRequestDto.setEndDate(pastDate);

        /// Act & Assert
        assertThatThrownBy(() -> programService.createProgram(testProgramRequestDto))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("End date cannot be before start date");
        verify(programRepository, never()).save(any());
    }

    @Test
    void shouldThrowBadRequestExceptionWhenStartDateIsBeforeToday() {
        /// Arrange
        testProgramRequestDto.setStartDate(pastDate);
        testProgramRequestDto.setEndDate(today);

        /// Act & Assert
        assertThatThrownBy(() -> programService.createProgram(testProgramRequestDto))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Start date cannot be before today");
        verify(programRepository, never()).save(any());
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenLearnerDoesNotExist() {
        /// Arrange
        testProgramRequestDto.setLearnerId(99L);

        /// Act & Assert
        assertThatThrownBy(() -> programService.createProgram(testProgramRequestDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Learner not found");
        verify(programRepository, never()).save(any());
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenTrainerDoesNotExist() {
        /// Arrange
        testProgramRequestDto.setTrainerId(99L);

        /// Act & Assert
        assertThatThrownBy(() -> programService.createProgram(testProgramRequestDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Trainer not found");
        verify(learnerRepository).findById(learnerId);
        verify(programRepository, never()).save(any());
    }

    @Test
    void shouldThrowDuplicateResourceExceptionWhenProgramNameExistsForLearner() {
        /// Arrange
        Program existingProg = new Program();
        existingProg.setName("Valid Program Name");
        testLearner.getPrograms().add(existingProg);


        /// Act & Assert
        assertThatThrownBy(() -> programService.createProgram(testProgramRequestDto))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("already exists for this learner");

        /// Verify Orden
        verify(learnerRepository).findById(learnerId);
        verify(trainerRepository, never()).findById(anyLong());
        verify(programRepository, never()).save(any());
    }

    // --- Tests para assignRoutineToProgram ---

    @Test
    void shouldAssignRoutineWhenProgramAndRoutineExistAndNotFinished() {
        /// Arrange (Happy Path)
        testProgram.setEndDate(futureDate);

        /// Act
        ProgramResponseDto result = programService.assignRoutineToProgram(programId, routineId);

        /// Assert
        assertThat(result).isNotNull();
        assertThat(testProgram.getProgramRoutines()).contains(testRoutine);

        /// Verify
        verify(programRepository).findById(programId);
        verify(routineRepository).findById(routineId);
        verify(programRepository).save(testProgram);
        verify(modelMapper).map(testProgram, ProgramResponseDto.class);
    }

    @Test
    void shouldAssignRoutineWhenProgramHasNullEndDate() {
        /// Arrange
        testProgram.setEndDate(null);

        /// Act
        ProgramResponseDto result = programService.assignRoutineToProgram(programId, routineId);

        /// Assert
        assertThat(result).isNotNull();
        assertThat(testProgram.getProgramRoutines()).contains(testRoutine);
        verify(programRepository).save(testProgram);
    }


    @Test
    void shouldThrowResourceNotFoundExceptionWhenAssigningToNonExistentProgram() {
        /// Arrange
        Long nonExistentId = 99L;

        /// Act & Assert
        assertThatThrownBy(() -> programService.assignRoutineToProgram(nonExistentId, routineId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Program not found");
        verify(programRepository, never()).save(any());
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenAssigningNonExistentRoutine() {
        /// Arrange
        Long nonExistentId = 99L;

        /// Act & Assert
        assertThatThrownBy(() -> programService.assignRoutineToProgram(programId, nonExistentId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Routine not found");
        verify(programRepository).findById(programId);
        verify(programRepository, never()).save(any());
    }

    @Test
    void shouldThrowDuplicateResourceExceptionWhenAssigningRoutineAlreadyInProgram() {
        /// Arrange
        testProgram.getProgramRoutines().add(testRoutine);

        /// Act & Assert
        assertThatThrownBy(() -> programService.assignRoutineToProgram(programId, routineId))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Routine already added");

        /// Verify
        verify(programRepository).findById(programId);
        verify(routineRepository).findById(routineId);
        verify(programRepository, never()).save(any());
    }

    @Test
    void shouldThrowInvalidOperationExceptionWhenAssigningRoutineToFinishedProgram() {
        /// Arrange
        testProgram.setEndDate(pastDate);

        /// Act & Assert
        assertThatThrownBy(() -> programService.assignRoutineToProgram(programId, routineId))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessageContaining("Cannot assign routine to a finished program");

        /// Verify
        verify(programRepository).findById(programId);
        verify(routineRepository).findById(routineId);
        verify(programRepository, never()).save(any());
    }

    // --- Tests para getProgramsByLearner ---

    @Test
    void shouldReturnPageOfProgramDtosWhenLearnerExists() {
        /// Arrange
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Program> programPage = new PageImpl<>(List.of(testProgram), pageRequest, 1);
        when(programRepository.findByLearner(testLearner, pageRequest)).thenReturn(programPage);

        /// Act
        Page<ProgramResponseDto> resultPage = programService.getProgramsByLearner(learnerId, pageRequest);

        /// Assert
        assertThat(resultPage).isNotNull();
        assertThat(resultPage.getTotalElements()).isEqualTo(1);
        assertThat(resultPage.getContent()).hasSize(1);
        assertThat(resultPage.getContent().get(0)).isEqualTo(testProgramResponseDto);

        /// Verify
        verify(learnerRepository).findById(learnerId);
        verify(programRepository).findByLearner(testLearner, pageRequest);
        verify(modelMapper, times(1)).map(any(Program.class), eq(ProgramResponseDto.class));
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenGettingProgramsForNonExistentLearner() {
        /// Arrange
        Long nonExistentId = 99L;
        PageRequest pageRequest = PageRequest.of(0, 10);

        /// Act & Assert
        assertThatThrownBy(() -> programService.getProgramsByLearner(nonExistentId, pageRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Learner not found");

        /// Verify
        verify(learnerRepository).findById(nonExistentId);
        verify(programRepository, never()).findByLearner(any(), any());
    }

    // --- Tests para deleteProgram ---

    @Test
    void shouldDeleteProgramWhenExistsAndIsNotActive() {
        /// Arrange
        testProgram.setStartDate(pastDate.minusDays(10));
        testProgram.setEndDate(pastDate);

        /// Act
        programService.deleteProgram(programId);

        /// Assert / Verify
        verify(programRepository).findById(programId);
        verify(programRepository).delete(testProgram);
    }

    @Test
    void shouldDeleteProgramWhenExistsAndDatesAreNull() {
        /// Arrange
        testProgram.setStartDate(null);
        testProgram.setEndDate(null);

        /// Act
        programService.deleteProgram(programId);

        /// Assert / Verify
        verify(programRepository).findById(programId);
        verify(programRepository).delete(testProgram);
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenDeletingNonExistentProgram() {
        /// Arrange
        Long nonExistentId = 99L;

        /// Act & Assert
        assertThatThrownBy(() -> programService.deleteProgram(nonExistentId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Program not found");

        /// Verify
        verify(programRepository).findById(nonExistentId);
        verify(programRepository, never()).delete(any());
    }

    @Test
    void shouldThrowInvalidOperationExceptionWhenDeletingActiveProgram() {
        /// Arrange
        testProgram.setStartDate(pastDate);
        testProgram.setEndDate(futureDate);

        /// Act & Assert
        assertThatThrownBy(() -> programService.deleteProgram(programId))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessageContaining("Cannot delete an active program");

        /// Verify
        verify(programRepository).findById(programId);
        verify(programRepository, never()).delete(any());
    }

    @Test
    void shouldNotThrowWhenDeletingProgramWithNullStartAndPastEnd() {
        /// Arrange
        testProgram.setStartDate(null);
        testProgram.setEndDate(pastDate);

        /// Act
        programService.deleteProgram(programId);

        /// Assert / Verify
        verify(programRepository).findById(programId);
        verify(programRepository).delete(testProgram);
    }
}