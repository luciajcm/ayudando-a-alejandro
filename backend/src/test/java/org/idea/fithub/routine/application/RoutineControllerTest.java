package org.idea.fithub.routine.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.idea.fithub.exceptions.*;
import org.idea.fithub.exercise.dto.ExerciseRequestDto;
import org.idea.fithub.global.GlobalExceptionHandler;
import org.idea.fithub.routine.domain.Day;
import org.idea.fithub.routine.domain.RoutineService;
import org.idea.fithub.routine.dto.RoutineExerciseDto;
import org.idea.fithub.routine.dto.RoutineRequestDto;
import org.idea.fithub.routine.dto.RoutineResponseDto;
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

@WebMvcTest(RoutineController.class)
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
class RoutineControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoutineService routineService;
    @MockBean
    private JwtService jwtService;
    @MockBean
    private UserService userService;
    @MockBean
    private ModelMapper modelMapper;

    @Autowired
    private ObjectMapper objectMapper;

    // --- Datos de Prueba ---
    private RoutineRequestDto testRoutineRequestDto;
    private RoutineResponseDto testRoutineResponseDto;
    private RoutineExerciseDto testRoutineExerciseDto;
    private Long routineId = 1L;
    private Long exerciseId = 10L;

    @BeforeEach
    void setUp() {
        testRoutineRequestDto = new RoutineRequestDto();
        testRoutineRequestDto.setName("Leg Day");
        testRoutineRequestDto.setDay(Day.MONDAY);

        testRoutineResponseDto = new RoutineResponseDto();
        testRoutineResponseDto.setId(routineId);
        testRoutineResponseDto.setName("Leg Day");
        testRoutineResponseDto.setDay(Day.MONDAY);

        ExerciseRequestDto exReqDto = new ExerciseRequestDto(); exReqDto.setName("Squat");
        exReqDto.setMuscle(org.idea.fithub.exercise.domain.Muscle.LEGS);
        testRoutineExerciseDto = new RoutineExerciseDto();
        testRoutineExerciseDto.setExerciseRequestDto(exReqDto);
        testRoutineExerciseDto.setSets(3);
        testRoutineExerciseDto.setRepetition(10);
    }

    // --- Tests para createRoutine (POST /api/routines) ---
    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturn201CreatedWhenCreateRoutineSuccessful() throws Exception {
        /// Arrange
        org.idea.fithub.routine.domain.Routine createdRoutineEntity = new org.idea.fithub.routine.domain.Routine();
        createdRoutineEntity.setId(routineId);
        when(routineService.createRoutine(any(RoutineRequestDto.class))).thenReturn(createdRoutineEntity);
        when(modelMapper.map(createdRoutineEntity, RoutineResponseDto.class)).thenReturn(testRoutineResponseDto);

        /// Act & Assert
        mockMvc.perform(post("/api/routines")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRoutineRequestDto))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id", is(testRoutineResponseDto.getId().intValue())))
                .andExpect(jsonPath("$.name", is(testRoutineResponseDto.getName())));

        /// Verify
        verify(routineService).createRoutine(any(RoutineRequestDto.class));
        verify(modelMapper).map(createdRoutineEntity, RoutineResponseDto.class);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturn409ConflictWhenCreateRoutineThrowsDuplicateResource() throws Exception {
        /// Arrange
        String errorMessage = "Routine already exists";
        when(routineService.createRoutine(any(RoutineRequestDto.class)))
                .thenThrow(new DuplicateResourceException(errorMessage));

        /// Act & Assert
        mockMvc.perform(post("/api/routines")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRoutineRequestDto))
                        .with(csrf()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", is(errorMessage)));
        /// Verify
        verify(routineService).createRoutine(any(RoutineRequestDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturn400BadRequestWhenCreateRoutineDtoIsInvalid() throws Exception {
        /// Arrange
        testRoutineRequestDto.setDay(null);

        /// Act & Assert
        mockMvc.perform(post("/api/routines")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRoutineRequestDto))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("day")));
        /// Verify
        verify(routineService, never()).createRoutine(any());
    }


    // --- Tests para getRoutine (GET /api/routines/{id}) ---
    @Test
    @WithMockUser
    void shouldReturn200OkAndRoutineDtoWhenGetRoutineById() throws Exception {
        /// Arrange
        when(routineService.getRoutine(routineId)).thenReturn(testRoutineResponseDto);

        /// Act & Assert
        mockMvc.perform(get("/api/routines/{id}", routineId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(routineId.intValue())))
                .andExpect(jsonPath("$.name", is(testRoutineResponseDto.getName())));
        /// Verify
        verify(routineService).getRoutine(routineId);
    }

    @Test
    @WithMockUser
    void shouldReturn404NotFoundWhenGetRoutineByIdNotFound() throws Exception {
        /// Arrange
        Long nonExistentId = 99L;
        String errorMessage = "Routine not found";
        when(routineService.getRoutine(nonExistentId)).thenThrow(new ResourceNotFoundException(errorMessage));

        /// Act & Assert
        mockMvc.perform(get("/api/routines/{id}", nonExistentId))
                .andExpect(status().isNotFound()) // GHE
                .andExpect(jsonPath("$.message", is(errorMessage)));
        /// Verify
        verify(routineService).getRoutine(nonExistentId);
    }

    // --- Tests para getAllRoutines (GET /api/routines) ---
    @Test
    @WithMockUser
    void shouldReturn200OkAndPageWhenGetAllRoutines() throws Exception {
        /// Arrange
        Page<RoutineResponseDto> routinePage = new PageImpl<>(List.of(testRoutineResponseDto));
        when(routineService.getAllRoutines(any(PageRequest.class))).thenReturn(routinePage);

        /// Act & Assert
        mockMvc.perform(get("/api/routines")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id", is(routineId.intValue())))
                .andExpect(jsonPath("$.totalElements", is(1)));
        /// Verify
        verify(routineService).getAllRoutines(PageRequest.of(0, 5));
    }


    // --- Tests para updateRoutine (PUT /api/routines/{id}) ---
    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturn200OkWhenUpdateRoutineSuccessful() throws Exception {
        /// Arrange
        when(routineService.updateRoutine(eq(routineId), any(RoutineRequestDto.class)))
                .thenReturn(testRoutineResponseDto);

        /// Act & Assert
        mockMvc.perform(put("/api/routines/{id}", routineId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRoutineRequestDto))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(routineId.intValue())));
        /// Verify
        verify(routineService).updateRoutine(eq(routineId), any(RoutineRequestDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturn409ConflictWhenUpdateRoutineThrowsDuplicateResource() throws Exception {
        /// Arrange
        String errorMessage = "Routine already exists";
        when(routineService.updateRoutine(eq(routineId), any(RoutineRequestDto.class)))
                .thenThrow(new DuplicateResourceException(errorMessage));

        /// Act & Assert
        mockMvc.perform(put("/api/routines/{id}", routineId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRoutineRequestDto))
                        .with(csrf()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", is(errorMessage)));
        /// Verify
        verify(routineService).updateRoutine(eq(routineId), any(RoutineRequestDto.class));
    }


    // --- Tests para deleteRoutine (DELETE /api/routines/{id}) ---
    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturn204NoContentWhenDeleteRoutineSuccessful() throws Exception {
        /// Arrange
        doNothing().when(routineService).deleteRoutine(routineId);

        /// Act & Assert
        mockMvc.perform(delete("/api/routines/{id}", routineId)
                        .with(csrf()))
                .andExpect(status().isNoContent());
        /// Verify
        verify(routineService).deleteRoutine(routineId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturn400BadRequestWhenDeleteRoutineThrowsInvalidOperation() throws Exception {
        /// Arrange
        String errorMessage = "Cannot delete routine in use";
        doThrow(new InvalidOperationException(errorMessage)).when(routineService).deleteRoutine(routineId);

        /// Act & Assert
        mockMvc.perform(delete("/api/routines/{id}", routineId)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is(errorMessage)));
        /// Verify
        verify(routineService).deleteRoutine(routineId);
    }


    // --- Tests para addExercise (POST /api/routines/{id}/exercise) ---
    @Test
    @WithMockUser(roles = {"ADMIN", "TRAINER"})
    void shouldReturn200OkWhenAddExerciseSuccessful() throws Exception {
        /// Arrange
        org.idea.fithub.routine.domain.Routine updatedRoutineEntity = new org.idea.fithub.routine.domain.Routine();
        updatedRoutineEntity.setId(routineId);
        when(routineService.addExercise(eq(routineId), any(RoutineExerciseDto.class)))
                .thenReturn(updatedRoutineEntity);
        when(modelMapper.map(updatedRoutineEntity, RoutineResponseDto.class)).thenReturn(testRoutineResponseDto);

        /// Act & Assert
        mockMvc.perform(post("/api/routines/{id}/exercise", routineId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRoutineExerciseDto))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id", is(testRoutineResponseDto.getId().intValue())));

        /// Verify
        verify(routineService).addExercise(eq(routineId), any(RoutineExerciseDto.class));
        verify(modelMapper).map(updatedRoutineEntity, RoutineResponseDto.class);
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "TRAINER"})
    void shouldReturn409ConflictWhenAddExerciseThrowsDuplicateResource() throws Exception {
        /// Arrange
        String errorMessage = "Exercise already in routine";
        when(routineService.addExercise(eq(routineId), any(RoutineExerciseDto.class)))
                .thenThrow(new DuplicateResourceException(errorMessage));

        /// Act & Assert
        mockMvc.perform(post("/api/routines/{id}/exercise", routineId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRoutineExerciseDto))
                        .with(csrf()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", is(errorMessage)));
        /// Verify
        verify(routineService).addExercise(eq(routineId), any(RoutineExerciseDto.class));
    }

    // --- Tests para deleteExercise (DELETE /api/routines/{routineId}/exercise/{exerciseId}) ---
    @Test
    @WithMockUser(roles = {"ADMIN", "TRAINER"})
    void shouldReturn204NoContentWhenDeleteExerciseSuccessful() throws Exception {
        /// Arrange
        doNothing().when(routineService).deleteExercise(routineId, exerciseId);

        /// Act & Assert
        mockMvc.perform(delete("/api/routines/{routineId}/exercise/{exerciseId}", routineId, exerciseId)
                        .with(csrf()))
                .andExpect(status().isNoContent());
        /// Verify
        verify(routineService).deleteExercise(routineId, exerciseId);
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "TRAINER"})
    void shouldReturn404NotFoundWhenDeleteExerciseThrowsResourceNotFound() throws Exception {
        /// Arrange
        String errorMessage = "Exercise not found in routine";
        doThrow(new ResourceNotFoundException(errorMessage)).when(routineService).deleteExercise(routineId, exerciseId);

        /// Act & Assert
        mockMvc.perform(delete("/api/routines/{routineId}/exercise/{exerciseId}", routineId, exerciseId)
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(errorMessage)));
        /// Verify
        verify(routineService).deleteExercise(routineId, exerciseId);
    }


    // --- Tests para getRoutineExercises (GET /api/routines/{id}/exercise) ---
    @Test
    @WithMockUser
    void shouldReturn200OkAndListOfExerciseDtos() throws Exception {
        /// Arrange
        List<RoutineExerciseDto> exerciseList = List.of(testRoutineExerciseDto);
        when(routineService.getRoutineExercises(eq(routineId), any(PageRequest.class)))
                .thenReturn(exerciseList);

        /// Act & Assert
        mockMvc.perform(get("/api/routines/{id}/exercise", routineId)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()", is(1)))
                .andExpect(jsonPath("$[0].sets", is(testRoutineExerciseDto.getSets())));

        /// Verify
        verify(routineService).getRoutineExercises(eq(routineId), eq(PageRequest.of(0, 10)));
    }
}