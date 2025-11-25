package org.idea.fithub.trainer.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.idea.fithub.exceptions.*;
import org.idea.fithub.global.GlobalExceptionHandler;
import org.idea.fithub.security.auth.jwt.JwtService;
import org.idea.fithub.trainer.domain.Trainer;
import org.idea.fithub.trainer.domain.TrainerService;
import org.idea.fithub.trainer.dto.TrainerExperienceDto;
import org.idea.fithub.trainer.dto.TrainerRequestDto;
import org.idea.fithub.trainer.dto.TrainerResponseDto;
import org.idea.fithub.user.domain.Gender;
import org.idea.fithub.user.domain.Role;
import org.idea.fithub.user.domain.UserStatus;
import org.idea.fithub.user.domain.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;

@WebMvcTest(TrainerController.class)
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
class TrainerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TrainerService trainerService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserService userService;

    @MockBean
    private ModelMapper modelMapper;

    @Autowired
    private ObjectMapper objectMapper;

    private TrainerResponseDto testTrainerResponseDto;
    private TrainerRequestDto testTrainerRequestDto;
    private TrainerExperienceDto testTrainerExperienceDto;
    private Trainer testTrainer;
    private Long trainerId = 1L;

    @BeforeEach
    void setUp() {
        // Setup TrainerResponseDto
        testTrainerResponseDto = new TrainerResponseDto();
        testTrainerResponseDto.setId(trainerId);
        testTrainerResponseDto.setFirstName("Test");
        testTrainerResponseDto.setLastName("Trainer");
        testTrainerResponseDto.setEmail("test@trainer.com");
        testTrainerResponseDto.setGender(Gender.MALE);
        testTrainerResponseDto.setPhoneNumber("987654321");
        testTrainerResponseDto.setRole(Role.TRAINER);
        testTrainerResponseDto.setUserStatus(UserStatus.AVAILABLE);

        // Setup TrainerRequestDto
        testTrainerRequestDto = new TrainerRequestDto();
        testTrainerRequestDto.setFirstName("Test");
        testTrainerRequestDto.setLastName("Trainer");
        testTrainerRequestDto.setEmail("test@trainer.com");
        testTrainerRequestDto.setPassword("password");
        testTrainerRequestDto.setGender(Gender.MALE);
        testTrainerRequestDto.setPhoneNumber("987654321");
        testTrainerRequestDto.setRole(Role.TRAINER);
        testTrainerRequestDto.setHeight(180.0);
        testTrainerRequestDto.setWeight(80.0);

        // Setup TrainerExperienceDto
        testTrainerExperienceDto = new TrainerExperienceDto();
        testTrainerExperienceDto.setExperienceStartDate(LocalDate.now().minusYears(1));

        // Setup Trainer entity
        testTrainer = new Trainer();
        testTrainer.setId(trainerId);
        testTrainer.setFirstName("Test");
        testTrainer.setLastName("Trainer");
        testTrainer.setEmail("test@trainer.com");
    }

    // --- Tests para getAllTrainers (GET /api/trainers) ---
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void shouldReturn200OkAndPageWhenGetAllTrainers() throws Exception {
        // Arrange
        Page<TrainerResponseDto> trainerPage = new PageImpl<>(List.of(testTrainerResponseDto));
        when(trainerService.getAllTrainers(any(PageRequest.class))).thenReturn(trainerPage);

        // Act & Assert
        mockMvc.perform(get("/api/trainers")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].id", is(trainerId.intValue())))
                .andExpect(jsonPath("$.totalElements", is(1)));

        // Verify
        verify(trainerService).getAllTrainers(PageRequest.of(0, 10));
    }

    // --- Tests para getTrainer (GET /api/trainers/{id}) ---
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void shouldReturn200OkAndTrainerDtoWhenGetTrainerById() throws Exception {
        // Arrange
        when(trainerService.getTrainer(trainerId)).thenReturn(testTrainerResponseDto);

        // Act & Assert
        mockMvc.perform(get("/api/trainers/{id}", trainerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(trainerId.intValue())))
                .andExpect(jsonPath("$.email", is(testTrainerResponseDto.getEmail())));

        // Verify
        verify(trainerService).getTrainer(trainerId);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void shouldReturn404NotFoundWhenGetTrainerByIdNotFound() throws Exception {
        // Arrange
        Long nonExistentId = 99L;
        String errorMessage = "Trainer not found with id: " + nonExistentId;
        when(trainerService.getTrainer(nonExistentId)).thenThrow(new ResourceNotFoundException(errorMessage));

        // Act & Assert
        mockMvc.perform(get("/api/trainers/{id}", nonExistentId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(errorMessage)));

        // Verify
        verify(trainerService).getTrainer(nonExistentId);
    }

    // --- Tests para createTrainer (POST /api/trainers) ---
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void shouldReturn201CreatedWhenCreateTrainerSuccessful() throws Exception {
        // Arrange
        when(trainerService.createTrainer(any(TrainerRequestDto.class))).thenReturn(testTrainer);
        when(modelMapper.map(testTrainer, TrainerResponseDto.class)).thenReturn(testTrainerResponseDto);

        // Act & Assert
        mockMvc.perform(post("/api/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testTrainerRequestDto))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id", is(trainerId.intValue())))
                .andExpect(jsonPath("$.email", is(testTrainerResponseDto.getEmail())));

        // Verify
        verify(trainerService).createTrainer(any(TrainerRequestDto.class));
        verify(modelMapper).map(testTrainer, TrainerResponseDto.class);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void shouldReturn409ConflictWhenCreateTrainerThrowsDuplicateResourceException() throws Exception {
        // Arrange
        String errorMessage = "Email already in use";
        when(trainerService.createTrainer(any(TrainerRequestDto.class)))
                .thenThrow(new DuplicateResourceException(errorMessage));

        // Act & Assert
        mockMvc.perform(post("/api/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testTrainerRequestDto))
                        .with(csrf()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", is(errorMessage)));

        // Verify
        verify(trainerService).createTrainer(any(TrainerRequestDto.class));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void shouldReturn400BadRequestWhenCreateTrainerDtoIsInvalid() throws Exception {
        // Arrange
        testTrainerRequestDto.setEmail(null); // Email es requerido
        testTrainerRequestDto.setHeight(null); // Height es requerido
        testTrainerRequestDto.setWeight(null); // Weight es requerido

        // Act & Assert
        mockMvc.perform(post("/api/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testTrainerRequestDto))
                        .with(csrf()))
                .andExpect(status().isBadRequest());

        // Verify
        verify(trainerService, never()).createTrainer(any());
    }

    // --- Tests para deleteTrainer (DELETE /api/trainers/{id}) ---
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void shouldReturn204NoContentWhenDeleteTrainerSuccessful() throws Exception {
        // Arrange
        doNothing().when(trainerService).deleteTrainer(trainerId);

        // Act & Assert
        mockMvc.perform(delete("/api/trainers/{id}", trainerId)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        // Verify
        verify(trainerService).deleteTrainer(trainerId);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void shouldReturn400BadRequestWhenDeleteTrainerThrowsInvalidOperationException() throws Exception {
        // Arrange
        String errorMessage = "Cannot delete trainer with assigned programs";
        doThrow(new InvalidOperationException(errorMessage)).when(trainerService).deleteTrainer(trainerId);

        // Act & Assert
        mockMvc.perform(delete("/api/trainers/{id}", trainerId)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is(errorMessage)));

        // Verify
        verify(trainerService).deleteTrainer(trainerId);
    }

    // --- Tests para updateTrainer (PUT /api/trainers/{id}) ---
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void shouldReturn200OkWhenUpdateTrainerSuccessful() throws Exception {
        // Arrange
        when(trainerService.updateTrainer(eq(trainerId), any(TrainerRequestDto.class)))
                .thenReturn(testTrainerResponseDto);

        // Act & Assert
        mockMvc.perform(put("/api/trainers/{id}", trainerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testTrainerRequestDto))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(trainerId.intValue())));

        // Verify
        verify(trainerService).updateTrainer(eq(trainerId), any(TrainerRequestDto.class));
    }

    // --- Tests para updateTrainerStatus (PATCH /api/trainers/{id}/status) ---
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void shouldReturn200OkWhenUpdateStatusSuccessful() throws Exception {
        // Arrange
        UserStatus newStatus = UserStatus.BUSY;
        when(trainerService.updateTrainerStatus(trainerId, newStatus)).thenReturn(testTrainerResponseDto);

        // Act & Assert
        mockMvc.perform(patch("/api/trainers/{id}/status", trainerId)
                        .param("userStatus", newStatus.name())
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(trainerId.intValue())));

        // Verify
        verify(trainerService).updateTrainerStatus(trainerId, newStatus);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void shouldReturn400BadRequestWhenUpdateStatusWithInvalidValue() throws Exception {
        // Arrange
        String invalidStatus = "NON_EXISTENT_STATUS";

        // Act & Assert
        mockMvc.perform(patch("/api/trainers/{id}/status", trainerId)
                        .param("userStatus", invalidStatus)
                        .with(csrf()))
                .andExpect(status().isBadRequest());

        // Verify
        verify(trainerService, never()).updateTrainerStatus(anyLong(), any());
    }

    // --- Tests para updateTrainerExperience (PATCH /api/trainers/{id}/experience) ---
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void shouldReturn200OkWhenUpdateExperienceSuccessful() throws Exception {
        // Arrange
        when(trainerService.updateTrainerExperience(eq(trainerId), any(TrainerExperienceDto.class)))
                .thenReturn(testTrainerResponseDto);

        // Act & Assert
        mockMvc.perform(patch("/api/trainers/{id}/experience", trainerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testTrainerExperienceDto))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(trainerId.intValue())));

        // Verify
        verify(trainerService).updateTrainerExperience(eq(trainerId), any(TrainerExperienceDto.class));
    }


    @Test
    void shouldReturn401UnauthorizedWhenAccessWithoutAuthentication() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/trainers"))
                .andExpect(status().isUnauthorized());
    }

}