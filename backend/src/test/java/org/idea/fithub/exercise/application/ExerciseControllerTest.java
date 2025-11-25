package org.idea.fithub.exercise.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.idea.fithub.exceptions.*;
import org.idea.fithub.exercise.domain.Exercise;
import org.idea.fithub.exercise.domain.ExerciseService;
import org.idea.fithub.exercise.domain.Muscle;
import org.idea.fithub.exercise.dto.ExerciseRequestDto;
import org.idea.fithub.exercise.dto.ExerciseResponseDto;
import org.idea.fithub.global.GlobalExceptionHandler;
import org.idea.fithub.security.auth.jwt.JwtService;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.containsString;


@WebMvcTest(ExerciseController.class)
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
class ExerciseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExerciseService exerciseService;
    @MockBean
    private JwtService jwtService;
    @MockBean
    private UserService userService;
    @MockBean
    private ModelMapper modelMapper;

    @Autowired
    private ObjectMapper objectMapper;

    private ExerciseRequestDto testExerciseRequestDto;
    private ExerciseResponseDto testExerciseResponseDto;
    private Long exerciseId = 1L;

    @BeforeEach
    void setUp() {
        testExerciseRequestDto = new ExerciseRequestDto();
        testExerciseRequestDto.setName("Bench Press");
        testExerciseRequestDto.setMuscle(Muscle.CHEST);

        testExerciseResponseDto = new ExerciseResponseDto();
        testExerciseResponseDto.setId(exerciseId);
        testExerciseResponseDto.setName("Bench Press");
        testExerciseResponseDto.setMuscle(Muscle.CHEST);

        lenient().when(modelMapper.map(any(Exercise.class), eq(ExerciseResponseDto.class)))
                .thenReturn(testExerciseResponseDto);
    }

    @Test
    @WithMockUser // Asumiendo que cualquier usuario autenticado puede ver
    void shouldReturn200OkAndPageWhenGetAllExercises() throws Exception {
        /// Arrange
        Page<ExerciseResponseDto> exercisePage = new PageImpl<>(List.of(testExerciseResponseDto));
        when(exerciseService.getAllExercises(any(PageRequest.class))).thenReturn(exercisePage);

        /// Act & Assert
        mockMvc.perform(get("/api/exercises")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id", is(exerciseId.intValue())))
                .andExpect(jsonPath("$.totalElements", is(1)));
        /// Verify
        verify(exerciseService).getAllExercises(PageRequest.of(0, 10));
    }

    @Test
    @WithMockUser
    void shouldReturn200OkAndExerciseDtoWhenGetExerciseById() throws Exception {
        /// Arrange
        when(exerciseService.getExercise(exerciseId)).thenReturn(testExerciseResponseDto);

        /// Act & Assert
        mockMvc.perform(get("/api/exercises/{id}", exerciseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(exerciseId.intValue())))
                .andExpect(jsonPath("$.name", is(testExerciseResponseDto.getName())));
        /// Verify
        verify(exerciseService).getExercise(exerciseId);
    }

    @Test
    @WithMockUser
    void shouldReturn404NotFoundWhenGetExerciseByIdNotFound() throws Exception {
        /// Arrange
        Long nonExistentId = 99L;
        String errorMessage = "Exercise not found";
        when(exerciseService.getExercise(nonExistentId)).thenThrow(new ResourceNotFoundException(errorMessage));

        /// Act & Assert
        mockMvc.perform(get("/api/exercises/{id}", nonExistentId))
                .andExpect(status().isNotFound()) // GHE
                .andExpect(jsonPath("$.message", is(errorMessage)));
        /// Verify
        verify(exerciseService).getExercise(nonExistentId);
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "TRAINER"}) // Asumiendo roles
    void shouldReturn201CreatedWhenCreateExerciseSuccessful() throws Exception {
        /// Arrange
        Exercise createdExerciseEntity = new Exercise();
        createdExerciseEntity.setId(exerciseId);
        when(exerciseService.createExercise(any(ExerciseRequestDto.class))).thenReturn(createdExerciseEntity);

        /// Act & Assert
        mockMvc.perform(post("/api/exercises")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testExerciseRequestDto))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id", is(testExerciseResponseDto.getId().intValue())))
                .andExpect(jsonPath("$.name", is(testExerciseResponseDto.getName())));

        /// Verify
        verify(exerciseService).createExercise(any(ExerciseRequestDto.class));
        verify(modelMapper).map(createdExerciseEntity, ExerciseResponseDto.class);
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "TRAINER"})
    void shouldReturn400BadRequestWhenCreateExerciseDtoIsInvalid() throws Exception {
        /// Arrange
        testExerciseRequestDto.setName(null);

        /// Act & Assert
        mockMvc.perform(post("/api/exercises")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testExerciseRequestDto))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("name")));
        /// Verify
        verify(exerciseService, never()).createExercise(any());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "TRAINER"})
    void shouldReturn409ConflictWhenCreateExerciseThrowsDuplicateResource() throws Exception {
        /// Arrange
        String errorMessage = "Exercise already exists";
        when(exerciseService.createExercise(any(ExerciseRequestDto.class)))
                .thenThrow(new DuplicateResourceException(errorMessage));

        /// Act & Assert
        mockMvc.perform(post("/api/exercises")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testExerciseRequestDto))
                        .with(csrf()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", is(errorMessage)));
        /// Verify
        verify(exerciseService).createExercise(any(ExerciseRequestDto.class));
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "TRAINER"})
    void shouldReturn200OkWhenUpdateExerciseSuccessful() throws Exception {
        /// Arrange
        when(exerciseService.updateExercise(eq(exerciseId), any(ExerciseRequestDto.class)))
                .thenReturn(testExerciseResponseDto);

        /// Act & Assert
        mockMvc.perform(put("/api/exercises/{id}", exerciseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testExerciseRequestDto))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(exerciseId.intValue())));
        /// Verify
        verify(exerciseService).updateExercise(eq(exerciseId), any(ExerciseRequestDto.class));
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "TRAINER"})
    void shouldReturn409ConflictWhenUpdateExerciseThrowsDuplicateResource() throws Exception {
        /// Arrange
        String errorMessage = "Exercise name conflict";
        when(exerciseService.updateExercise(eq(exerciseId), any(ExerciseRequestDto.class)))
                .thenThrow(new DuplicateResourceException(errorMessage));

        /// Act & Assert
        mockMvc.perform(put("/api/exercises/{id}", exerciseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testExerciseRequestDto))
                        .with(csrf()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", is(errorMessage)));
        /// Verify
        verify(exerciseService).updateExercise(eq(exerciseId), any(ExerciseRequestDto.class));
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "TRAINER"})
    void shouldReturn200OkWhenUpdateMuscleSuccessful() throws Exception {
        /// Arrange
        Muscle newMuscle = Muscle.BACK;
        when(exerciseService.updateExercise(exerciseId, newMuscle)).thenReturn(testExerciseResponseDto);

        /// Act & Assert
        mockMvc.perform(patch("/api/exercises/{id}/muscle", exerciseId)
                        .param("muscle", newMuscle.name())
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(exerciseId.intValue())));
        /// Verify
        verify(exerciseService).updateExercise(exerciseId, newMuscle);
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "TRAINER"})
    void shouldReturn400BadRequestWhenUpdateMuscleWithInvalidValue() throws Exception {
        /// Arrange
        String invalidMuscle = "NOT_A_MUSCLE";

        /// Act & Assert
        mockMvc.perform(patch("/api/exercises/{id}/muscle", exerciseId)
                        .param("muscle", invalidMuscle)
                        .with(csrf()))
                .andExpect(status().isBadRequest());
        /// Verify
        verify(exerciseService, never()).updateExercise(anyLong(), any(Muscle.class));
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "TRAINER"})
    void shouldReturn200OkWhenUpdateAssetSuccessful() throws Exception {
        /// Arrange
        String newAsset = "new_asset.png";
        when(exerciseService.updateExercise(exerciseId, newAsset)).thenReturn(testExerciseResponseDto);

        /// Act & Assert
        mockMvc.perform(patch("/api/exercises/{id}/asset", exerciseId)
                        .param("asset", newAsset)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(exerciseId.intValue())));
        /// Verify
        verify(exerciseService).updateExercise(exerciseId, newAsset);
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "TRAINER"})
    void shouldReturn400BadRequestWhenUpdateAssetThrowsBadRequest() throws Exception {
        /// Arrange
        String blankAsset = " ";
        String errorMessage = "Asset cannot be blank";
        when(exerciseService.updateExercise(exerciseId, blankAsset))
                .thenThrow(new BadRequestException(errorMessage));


        /// Act & Assert
        mockMvc.perform(patch("/api/exercises/{id}/asset", exerciseId)
                        .param("asset", blankAsset)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is(errorMessage)));
        /// Verify
        verify(exerciseService).updateExercise(exerciseId, blankAsset);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturn204NoContentWhenDeleteExerciseSuccessful() throws Exception {
        /// Arrange
        doNothing().when(exerciseService).deleteExercise(exerciseId);

        /// Act & Assert
        mockMvc.perform(delete("/api/exercises/{id}", exerciseId)
                        .with(csrf()))
                .andExpect(status().isNoContent());
        /// Verify
        verify(exerciseService).deleteExercise(exerciseId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturn409ConflictWhenDeleteExerciseThrowsConflictException() throws Exception {
        /// Arrange
        String errorMessage = "Cannot delete exercise in use";
        doThrow(new ConflictException(errorMessage)).when(exerciseService).deleteExercise(exerciseId);

        /// Act & Assert
        mockMvc.perform(delete("/api/exercises/{id}", exerciseId)
                        .with(csrf()))
                .andExpect(status().isConflict()) // GHE
                .andExpect(jsonPath("$.message", is(errorMessage)));
        /// Verify
        verify(exerciseService).deleteExercise(exerciseId);
    }
}