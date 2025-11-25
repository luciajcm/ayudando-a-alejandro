package org.idea.fithub.learner.domain;

import org.idea.fithub.exceptions.BadRequestException;
import org.idea.fithub.exceptions.ConflictException;
import org.idea.fithub.exceptions.DuplicateResourceException;
import org.idea.fithub.exceptions.ResourceNotFoundException;
import org.idea.fithub.learner.dto.LearnerRequestDto;
import org.idea.fithub.learner.dto.LearnerResponseDto;
import org.idea.fithub.learner.dto.LearnerStatsDto;
import org.idea.fithub.learner.infrastructure.LearnerRepository;
import org.idea.fithub.program.domain.Program;
import org.idea.fithub.trainer.domain.GoalType;
import org.idea.fithub.trainingCheck.domain.TrainingCheck;
import org.idea.fithub.user.domain.Gender;
import org.idea.fithub.user.domain.Role;
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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LearnerServiceTest {

    @Mock
    private LearnerRepository learnerRepository;
    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private LearnerService learnerService;

    // --- Datos de Prueba Comunes ---
    private Learner testLearner;
    private LearnerRequestDto testLearnerRequestDto;
    private LearnerResponseDto testLearnerResponseDto;
    private LearnerStatsDto testLearnerStatsDto;
    private Long learnerId = 1L;
    private String existingEmail = "existing@test.com";
    private String newEmail = "new@test.com";

    @BeforeEach
    void setUp() {
        /// Arrange global
        testLearner = new Learner();
        testLearner.setId(learnerId);
        testLearner.setEmail(existingEmail);
        testLearner.setFirstName("Test");
        testLearner.setLastName("Learner");
        testLearner.setPassword("password");
        testLearner.setUsername("testlearner");
        testLearner.setRole(Role.LEARNER);
        testLearner.setGender(Gender.MALE);
        testLearner.setPhoneNumber("123456");
        testLearner.setWeight(70.0);

        testLearner.setPrograms(new ArrayList<>());
        testLearner.setTrainingChecks(new ArrayList<>());

        testLearnerRequestDto = new LearnerRequestDto();
        testLearnerRequestDto.setEmail(newEmail);
        testLearnerRequestDto.setFirstName("Updated");

        testLearnerResponseDto = new LearnerResponseDto();
        testLearnerResponseDto.setId(learnerId);
        testLearnerResponseDto.setEmail(existingEmail);

        testLearnerStatsDto = new LearnerStatsDto();
        testLearnerStatsDto.setWeight(75.5);
        testLearnerStatsDto.setHeight(1.80);
        testLearnerStatsDto.setGoalType(GoalType.MUSCLE_MASS_GAIN);

        lenient().when(learnerRepository.findById(learnerId)).thenReturn(Optional.of(testLearner));
        lenient().when(learnerRepository.findById(99L)).thenReturn(Optional.empty());
        lenient().when(modelMapper.map(any(Learner.class), eq(LearnerResponseDto.class))).thenReturn(testLearnerResponseDto);
        lenient().when(modelMapper.map(any(LearnerRequestDto.class), eq(Learner.class))).thenReturn(new Learner());
        lenient().when(passwordEncoder.encode(any(CharSequence.class))).thenReturn("encodedPasswordFromMock");
        lenient().when(learnerRepository.save(any(Learner.class))).thenReturn(testLearner);
    }

    // --- Tests para getAllLearners ---
    @Test
    void shouldReturnMappedPageOfLearners() {
        /// Arrange
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Learner> learnerPage = new PageImpl<>(List.of(testLearner), pageRequest, 1);
        Page<LearnerResponseDto> expectedDtoPage = new PageImpl<>(List.of(testLearnerResponseDto), pageRequest, 1);

        when(learnerRepository.findAll(pageRequest)).thenReturn(learnerPage);
        when(modelMapper.map(testLearner, LearnerResponseDto.class)).thenReturn(testLearnerResponseDto);

        /// Act
        Page<LearnerResponseDto> resultPage = learnerService.getAllLearners(pageRequest);

        /// Assert
        assertThat(resultPage).isNotNull();
        assertThat(resultPage.getTotalElements()).isEqualTo(1);
        assertThat(resultPage.getContent()).hasSize(1);
        assertThat(resultPage.getContent().get(0)).isEqualTo(testLearnerResponseDto);

        /// Verify
        verify(learnerRepository).findAll(pageRequest);
        verify(modelMapper).map(testLearner, LearnerResponseDto.class);
    }

    // --- Tests para getLearner ---

    @Test
    void shouldReturnLearnerDtoWhenLearnerExists() {
        /// Arrange

        /// Act
        LearnerResponseDto result = learnerService.getLearner(learnerId);

        /// Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(learnerId);
        verify(learnerRepository).findById(learnerId);
        verify(modelMapper).map(testLearner, LearnerResponseDto.class);
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenGettingNonExistentLearner() {
        /// Arrange
        Long nonExistentId = 99L;

        /// Act & Assert
        assertThatThrownBy(() -> learnerService.getLearner(nonExistentId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Learner not found with id: " + nonExistentId);
        verify(learnerRepository).findById(nonExistentId);
        verify(modelMapper, never()).map(any(), any());
    }

    // --- Tests para createLearner ---

    @Test
    void shouldSaveLearnerWhenEmailNotDuplicate() {
        /// Arrange (Happy Path)
        Learner mappedLearner = new Learner();
        when(learnerRepository.existsByEmail(testLearnerRequestDto.getEmail())).thenReturn(false);
        when(modelMapper.map(testLearnerRequestDto, Learner.class)).thenReturn(mappedLearner);
        when(learnerRepository.save(mappedLearner)).thenReturn(mappedLearner);

        /// Act
        Learner result = learnerService.createLearner(testLearnerRequestDto);

        /// Assert
        assertThat(result).isNotNull();
        verify(learnerRepository).existsByEmail(testLearnerRequestDto.getEmail());
        verify(modelMapper).map(testLearnerRequestDto, Learner.class);
        verify(learnerRepository).save(mappedLearner);
    }

    @Test
    void shouldThrowDuplicateResourceExceptionWhenCreatingLearnerWithExistingEmail() {
        /// Arrange
        when(learnerRepository.existsByEmail(testLearnerRequestDto.getEmail())).thenReturn(true);

        /// Act & Assert
        assertThatThrownBy(() -> learnerService.createLearner(testLearnerRequestDto))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Email already in use: " + testLearnerRequestDto.getEmail());

        /// Verify
        verify(learnerRepository).existsByEmail(testLearnerRequestDto.getEmail());
        verify(modelMapper, never()).map(any(), any());
        verify(learnerRepository, never()).save(any());
    }

    // --- Tests para deleteLearner ---

    @Test
    void shouldDeleteLearnerWhenExistsAndHasNoRelations() {
        /// Arrange

        /// Act
        learnerService.deleteLearner(learnerId);

        /// Assert / Verify
        verify(learnerRepository).findById(learnerId);
        assertThat(testLearner.getPrograms()).isEmpty();
        assertThat(testLearner.getTrainingChecks()).isEmpty();
        verify(learnerRepository).delete(testLearner);
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenDeletingNonExistentLearner() {
        /// Arrange
        Long nonExistentId = 99L;

        /// Act & Assert
        assertThatThrownBy(() -> learnerService.deleteLearner(nonExistentId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Learner not found with id: " + nonExistentId);

        /// Verify
        verify(learnerRepository).findById(nonExistentId);
        verify(learnerRepository, never()).delete(any());
    }

    @Test
    void shouldThrowConflictExceptionWhenDeletingLearnerWithPrograms() {
        /// Arrange
        testLearner.getPrograms().add(new Program());

        /// Act & Assert
        assertThatThrownBy(() -> learnerService.deleteLearner(learnerId))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("active program(s)");

        /// Verify
        verify(learnerRepository).findById(learnerId);
        verify(learnerRepository, never()).delete(any());
    }

    @Test
    void shouldThrowConflictExceptionWhenDeletingLearnerWithTrainingChecks() {
        /// Arrange
        testLearner.getTrainingChecks().add(new TrainingCheck());

        /// Act & Assert
        assertThatThrownBy(() -> learnerService.deleteLearner(learnerId))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("training check(s)");

        /// Verify
        verify(learnerRepository).findById(learnerId);
        assertThat(testLearner.getPrograms()).isEmpty();
        verify(learnerRepository, never()).delete(any());
    }

    // --- Tests para updateLearner ---

    @Test
    void shouldUpdateLearnerWhenEmailIsNotChanged() {
        /// Arrange
        testLearnerRequestDto.setEmail(existingEmail);
        when(learnerRepository.findById(learnerId)).thenReturn(Optional.of(testLearner));

        /// Act
        LearnerResponseDto result = learnerService.updateLearner(learnerId, testLearnerRequestDto);

        /// Assert
        assertThat(result).isNotNull();
        verify(learnerRepository).findById(learnerId);
        verify(learnerRepository, never()).existsByEmail(anyString());
        verify(modelMapper).map(testLearnerRequestDto, testLearner);
        verify(learnerRepository).save(testLearner);
    }

    @Test
    void shouldUpdateLearnerWhenEmailIsChangedAndNotDuplicate() {
        /// Arrange
        testLearnerRequestDto.setEmail(newEmail);
        when(learnerRepository.findById(learnerId)).thenReturn(Optional.of(testLearner));
        when(learnerRepository.existsByEmail(newEmail)).thenReturn(false);

        /// Act
        LearnerResponseDto result = learnerService.updateLearner(learnerId, testLearnerRequestDto);

        /// Assert
        assertThat(result).isNotNull();
        verify(learnerRepository).findById(learnerId);
        verify(learnerRepository).existsByEmail(newEmail);
        verify(modelMapper).map(testLearnerRequestDto, testLearner);
        verify(learnerRepository).save(testLearner);
    }

    @Test
    void shouldThrowDuplicateResourceExceptionWhenUpdatingLearnerEmailToExistingOne() {
        /// Arrange
        String duplicateEmail = "another@taken.com";
        testLearnerRequestDto.setEmail(duplicateEmail);
        when(learnerRepository.findById(learnerId)).thenReturn(Optional.of(testLearner));
        when(learnerRepository.existsByEmail(duplicateEmail)).thenReturn(true);

        /// Act & Assert
        assertThatThrownBy(() -> learnerService.updateLearner(learnerId, testLearnerRequestDto))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Email already in use: " + duplicateEmail);

        /// Verify
        verify(learnerRepository).findById(learnerId);
        verify(learnerRepository).existsByEmail(duplicateEmail);
        verify(modelMapper, never()).map(eq(testLearnerRequestDto), any(Learner.class));
        verify(learnerRepository, never()).save(any());
    }

    // --- Tests para updateLearnerGoal ---

    @Test
    void shouldUpdateLearnerGoalWhenGoalIsValid() {
        /// Arrange
        GoalType newGoal = GoalType.LOSE_WEIGHT;

        /// Act
        LearnerResponseDto result = learnerService.updateLearnerGoal(learnerId, newGoal);

        /// Assert
        assertThat(result).isNotNull();
        assertThat(testLearner.getGoalType()).isEqualTo(newGoal);
        verify(learnerRepository).findById(learnerId);
        verify(learnerRepository).save(testLearner);
        verify(modelMapper).map(testLearner, LearnerResponseDto.class);
    }

    @Test
    void shouldThrowBadRequestExceptionWhenUpdatingGoalWithNull() {
        /// Arrange
        GoalType nullGoal = null;

        /// Act & Assert
        assertThatThrownBy(() -> learnerService.updateLearnerGoal(learnerId, nullGoal))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Goal type is required");

        /// Verify
        verify(learnerRepository).findById(learnerId);
        verify(learnerRepository, never()).save(any());
    }

    // --- Tests para updateLearnerStats ---

    @Test
    void shouldUpdateLearnerStatsWhenStatsAreValid() {
        /// Arrange

        /// Act
        LearnerResponseDto result = learnerService.updateLearnerStats(learnerId, testLearnerStatsDto);

        /// Assert
        assertThat(result).isNotNull();
        assertThat(testLearner.getWeight()).isEqualTo(testLearnerStatsDto.getWeight());
        assertThat(testLearner.getHeight()).isEqualTo(testLearnerStatsDto.getHeight());
        assertThat(testLearner.getGoalType()).isEqualTo(testLearnerStatsDto.getGoalType());
        verify(learnerRepository).findById(learnerId);
        verify(learnerRepository).save(testLearner);
        verify(modelMapper).map(testLearner, LearnerResponseDto.class);
    }

    @Test
    void shouldThrowBadRequestExceptionWhenUpdatingStatsWithNullWeight() {
        /// Arrange
        testLearnerStatsDto.setWeight(null);

        /// Act & Assert
        assertThatThrownBy(() -> learnerService.updateLearnerStats(learnerId, testLearnerStatsDto))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Weight must be greater than 0");
        verify(learnerRepository, never()).save(any());
    }

    @Test
    void shouldThrowBadRequestExceptionWhenUpdatingStatsWithZeroWeight() {
        /// Arrange
        testLearnerStatsDto.setWeight(0.0);

        /// Act & Assert
        assertThatThrownBy(() -> learnerService.updateLearnerStats(learnerId, testLearnerStatsDto))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Weight must be greater than 0");
        verify(learnerRepository, never()).save(any());
    }

    @Test
    void shouldThrowBadRequestExceptionWhenUpdatingStatsWithNullHeight() {
        /// Arrange
        testLearnerStatsDto.setHeight(null);

        /// Act & Assert
        assertThatThrownBy(() -> learnerService.updateLearnerStats(learnerId, testLearnerStatsDto))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Height must be greater than 0");
        verify(learnerRepository, never()).save(any());
    }

    @Test
    void shouldThrowBadRequestExceptionWhenUpdatingStatsWithNullGoalType() {
        /// Arrange
        testLearnerStatsDto.setGoalType(null);

        /// Act & Assert
        assertThatThrownBy(() -> learnerService.updateLearnerStats(learnerId, testLearnerStatsDto))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Goal type is required");
        assertThat(testLearnerStatsDto.getWeight()).isNotNull();
        assertThat(testLearnerStatsDto.getHeight()).isNotNull();
        verify(learnerRepository, never()).save(any());
    }
}