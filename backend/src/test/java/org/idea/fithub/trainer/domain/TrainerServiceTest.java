package org.idea.fithub.trainer.domain;

import org.idea.fithub.exceptions.BadRequestException;
import org.idea.fithub.exceptions.DuplicateResourceException;
import org.idea.fithub.exceptions.InvalidOperationException;
import org.idea.fithub.exceptions.ResourceNotFoundException;
import org.idea.fithub.program.domain.Program;
import org.idea.fithub.trainer.dto.TrainerExperienceDto;
import org.idea.fithub.trainer.dto.TrainerRequestDto;
import org.idea.fithub.trainer.dto.TrainerResponseDto;
import org.idea.fithub.trainer.infrastructure.TrainerRepository;
import org.idea.fithub.user.domain.Gender;
import org.idea.fithub.user.domain.Role;
import org.idea.fithub.user.domain.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

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
class TrainerServiceTest {

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private TrainerService trainerService;

    private Trainer testTrainer;
    private TrainerRequestDto testTrainerRequestDto;
    private TrainerResponseDto testTrainerResponseDto;
    private Long trainerId = 1L;
    private String existingEmail = "existing@test.com";
    private String newEmail = "new@test.com";

    @BeforeEach
    void setUp() {
        testTrainer = new Trainer();
        testTrainer.setId(trainerId);
        testTrainer.setEmail(existingEmail);
        testTrainer.setFirstName("Test");
        testTrainer.setLastName("Trainer");
        testTrainer.setRole(Role.TRAINER);
        testTrainer.setGender(Gender.MALE);
        testTrainer.setPhoneNumber("123456");

        testTrainerRequestDto = new TrainerRequestDto();
        testTrainerRequestDto.setEmail(newEmail);
        testTrainerRequestDto.setFirstName("Updated");
        testTrainerRequestDto.setLastName("Name");
        testTrainerRequestDto.setPassword("password123");

        testTrainerResponseDto = new TrainerResponseDto();
        testTrainerResponseDto.setId(trainerId);
        testTrainerResponseDto.setEmail(newEmail);

        lenient().when(trainerRepository.findById(trainerId)).thenReturn(Optional.of(testTrainer));
        lenient().when(trainerRepository.findById(99L)).thenReturn(Optional.empty());
        lenient().when(modelMapper.map(any(TrainerRequestDto.class), eq(Trainer.class))).thenReturn(new Trainer());
        lenient().when(modelMapper.map(any(Trainer.class), eq(TrainerResponseDto.class))).thenReturn(new TrainerResponseDto());
        lenient().when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
    }

    @Test
    void shouldReturnTrainerDtoWhenTrainerExists() {
        when(modelMapper.map(testTrainer, TrainerResponseDto.class)).thenReturn(testTrainerResponseDto);

        TrainerResponseDto result = trainerService.getTrainer(trainerId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(trainerId);
        verify(trainerRepository).findById(trainerId);
        verify(modelMapper).map(testTrainer, TrainerResponseDto.class);
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenGettingNonExistentTrainer() {
        Long nonExistentId = 99L;

        assertThatThrownBy(() -> trainerService.getTrainer(nonExistentId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Trainer not found with id: " + nonExistentId);
        verify(trainerRepository).findById(nonExistentId);
        verify(modelMapper, never()).map(any(), any());
    }

    @Test
    void shouldSaveAndReturnTrainerWhenDataIsValidAndEmailNotDuplicate() {
        Trainer mappedTrainer = new Trainer();
        LocalDate startDate = LocalDate.now().minusYears(1);
        LocalDate endDate = LocalDate.now();
        mappedTrainer.setExperienceStartDate(startDate);
        mappedTrainer.setExperienceEndDate(endDate);

        when(trainerRepository.existsByEmail(testTrainerRequestDto.getEmail())).thenReturn(false);
        when(modelMapper.map(testTrainerRequestDto, Trainer.class)).thenReturn(mappedTrainer);
        when(trainerRepository.save(mappedTrainer)).thenReturn(mappedTrainer);

        Trainer result = trainerService.createTrainer(testTrainerRequestDto);

        assertThat(result).isNotNull();
        verify(trainerRepository).existsByEmail(testTrainerRequestDto.getEmail());
        verify(modelMapper).map(testTrainerRequestDto, Trainer.class);
        verify(passwordEncoder).encode(testTrainerRequestDto.getPassword());
        verify(trainerRepository).save(mappedTrainer);
    }

    @Test
    void shouldThrowDuplicateResourceExceptionWhenCreatingTrainerWithExistingEmail() {
        when(trainerRepository.existsByEmail(testTrainerRequestDto.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> trainerService.createTrainer(testTrainerRequestDto))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Email already in use: " + testTrainerRequestDto.getEmail());

        verify(trainerRepository).existsByEmail(testTrainerRequestDto.getEmail());
        verify(modelMapper, never()).map(any(), any());
        verify(trainerRepository, never()).save(any());
    }

    @Test
    void shouldThrowBadRequestExceptionWhenCreatingTrainerWithEndDateBeforeStartDate() {
        testTrainerRequestDto.setExperienceStartDate(LocalDate.now());
        testTrainerRequestDto.setExperienceEndDate(LocalDate.now().minusDays(1));
        when(trainerRepository.existsByEmail(testTrainerRequestDto.getEmail())).thenReturn(false);

        assertThatThrownBy(() -> trainerService.createTrainer(testTrainerRequestDto))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("End date cannot be before start date");

        verify(trainerRepository).existsByEmail(testTrainerRequestDto.getEmail());
        verify(modelMapper, never()).map(any(), any());
        verify(trainerRepository, never()).save(any());
    }

    @Test
    void shouldDeleteTrainerWhenTrainerExistsAndHasNoPrograms() {
        testTrainer.setPrograms(new ArrayList<>());

        trainerService.deleteTrainer(trainerId);

        verify(trainerRepository).findById(trainerId);
        verify(trainerRepository).delete(testTrainer);
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenDeletingNonExistentTrainer() {
        Long nonExistentId = 99L;

        assertThatThrownBy(() -> trainerService.deleteTrainer(nonExistentId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Trainer not found with id: " + nonExistentId);

        verify(trainerRepository).findById(nonExistentId);
        verify(trainerRepository, never()).delete(any());
    }

    @Test
    void shouldThrowInvalidOperationExceptionWhenDeletingTrainerWithAssignedPrograms() {
        List<Program> programs = List.of(new Program());
        testTrainer.setPrograms(programs);

        assertThatThrownBy(() -> trainerService.deleteTrainer(trainerId))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessageContaining("Cannot delete trainer with assigned programs");

        verify(trainerRepository).findById(trainerId);
        verify(trainerRepository, never()).delete(any());
    }

    @Test
    void shouldUpdateTrainerWhenEmailIsNotChanged() {
        testTrainerRequestDto.setEmail(existingEmail);
        when(trainerRepository.save(testTrainer)).thenReturn(testTrainer);
        when(modelMapper.map(testTrainer, TrainerResponseDto.class)).thenReturn(testTrainerResponseDto);

        doNothing().when(modelMapper).map(testTrainerRequestDto, testTrainer);

        TrainerResponseDto result = trainerService.updateTrainer(trainerId, testTrainerRequestDto);

        assertThat(result).isNotNull();
        verify(trainerRepository).findById(trainerId);
        verify(trainerRepository, never()).existsByEmail(anyString());
        verify(modelMapper).map(testTrainerRequestDto, testTrainer);
        verify(passwordEncoder).encode(testTrainerRequestDto.getPassword());
        verify(trainerRepository).save(testTrainer);
        verify(modelMapper).map(testTrainer, TrainerResponseDto.class);
    }

    @Test
    void shouldUpdateTrainerWhenEmailIsChangedAndNotDuplicate() {
        testTrainerRequestDto.setEmail(newEmail);
        when(trainerRepository.existsByEmail(newEmail)).thenReturn(false);
        when(trainerRepository.save(testTrainer)).thenReturn(testTrainer);
        when(modelMapper.map(testTrainer, TrainerResponseDto.class)).thenReturn(testTrainerResponseDto);

        doNothing().when(modelMapper).map(testTrainerRequestDto, testTrainer);

        TrainerResponseDto result = trainerService.updateTrainer(trainerId, testTrainerRequestDto);

        assertThat(result).isNotNull();
        verify(trainerRepository).findById(trainerId);
        verify(trainerRepository).existsByEmail(newEmail);
        verify(modelMapper).map(testTrainerRequestDto, testTrainer);
        verify(passwordEncoder).encode(testTrainerRequestDto.getPassword());
        verify(trainerRepository).save(testTrainer);
        verify(modelMapper).map(testTrainer, TrainerResponseDto.class);
    }

    @Test
    void shouldThrowDuplicateResourceExceptionWhenUpdatingTrainerEmailToExistingOne() {
        String duplicateEmail = "another@taken.com";
        testTrainerRequestDto.setEmail(duplicateEmail);
        when(trainerRepository.existsByEmail(duplicateEmail)).thenReturn(true);

        assertThatThrownBy(() -> trainerService.updateTrainer(trainerId, testTrainerRequestDto))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Email already in use: " + duplicateEmail);

        verify(trainerRepository).findById(trainerId);
        verify(trainerRepository).existsByEmail(duplicateEmail);
        verify(modelMapper, never()).map(eq(testTrainerRequestDto), any(Trainer.class));
        verify(trainerRepository, never()).save(any());
    }

    @Test
    void shouldUpdateTrainerStatusWhenTrainerExists() {
        UserStatus newStatus = UserStatus.BUSY;
        when(trainerRepository.save(testTrainer)).thenReturn(testTrainer);
        when(modelMapper.map(testTrainer, TrainerResponseDto.class)).thenReturn(testTrainerResponseDto);

        TrainerResponseDto result = trainerService.updateTrainerStatus(trainerId, newStatus);

        assertThat(result).isNotNull();
        assertThat(testTrainer.getUserStatus()).isEqualTo(newStatus);
        verify(trainerRepository).findById(trainerId);
        verify(trainerRepository).save(testTrainer);
        verify(modelMapper).map(testTrainer, TrainerResponseDto.class);
    }

    @Test
    void shouldThrowBadRequestExceptionWhenUpdatingTrainerStatusWithNull() {
        UserStatus nullStatus = null;

        assertThatThrownBy(() -> trainerService.updateTrainerStatus(trainerId, nullStatus))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("User status is required");

        verify(trainerRepository).findById(trainerId);
        verify(trainerRepository, never()).save(any());
    }

    @Test
    void shouldUpdateTrainerExperienceWhenDataIsValid() {
        TrainerExperienceDto experienceDto = new TrainerExperienceDto();
        LocalDate startDate = LocalDate.now().minusYears(2);
        LocalDate endDate = LocalDate.now().minusMonths(6);
        experienceDto.setExperienceStartDate(startDate);
        experienceDto.setExperienceEndDate(endDate);

        when(trainerRepository.save(testTrainer)).thenReturn(testTrainer);
        when(modelMapper.map(testTrainer, TrainerResponseDto.class)).thenReturn(testTrainerResponseDto);

        TrainerResponseDto result = trainerService.updateTrainerExperience(trainerId, experienceDto);

        assertThat(result).isNotNull();
        assertThat(testTrainer.getExperienceStartDate()).isEqualTo(startDate);
        assertThat(testTrainer.getExperienceEndDate()).isEqualTo(endDate);
        assertThat(testTrainer.getExperienceTime()).isNotNull();
        verify(trainerRepository).findById(trainerId);
        verify(trainerRepository).save(testTrainer);
        verify(modelMapper).map(testTrainer, TrainerResponseDto.class);
    }

    @Test
    void shouldThrowBadRequestExceptionWhenUpdatingTrainerExperienceWithNullStartDate() {
        TrainerExperienceDto experienceDto = new TrainerExperienceDto();
        experienceDto.setExperienceStartDate(null);

        assertThatThrownBy(() -> trainerService.updateTrainerExperience(trainerId, experienceDto))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Experience start date is required");

        verify(trainerRepository).findById(trainerId);
        verify(trainerRepository, never()).save(any());
    }
}