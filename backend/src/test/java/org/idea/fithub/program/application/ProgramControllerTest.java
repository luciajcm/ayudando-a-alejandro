package org.idea.fithub.program.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.idea.fithub.exceptions.*;
import org.idea.fithub.global.GlobalExceptionHandler;
import org.idea.fithub.program.domain.ProgramService;
import org.idea.fithub.program.dto.ProgramRequestDto;
import org.idea.fithub.program.dto.ProgramResponseDto;
import org.idea.fithub.security.auth.jwt.JwtService;
import org.idea.fithub.user.domain.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.containsString;

@WebMvcTest(ProgramController.class)
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
class ProgramControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProgramService programService;
    @MockBean
    private JwtService jwtService;
    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    // --- Datos de Prueba ---
    private ProgramRequestDto testProgramRequestDto;
    private ProgramResponseDto testProgramResponseDto;
    private Long programId = 1L;
    private Long learnerId = 10L;
    private Long trainerId = 100L;
    private Long routineId = 1000L;

    @BeforeEach
    void setUp() {
        testProgramRequestDto = new ProgramRequestDto();
        testProgramRequestDto.setLearnerId(learnerId);
        testProgramRequestDto.setTrainerId(trainerId);
        testProgramRequestDto.setName("New Program");
        testProgramRequestDto.setStartDate(LocalDate.now());

        testProgramResponseDto = new ProgramResponseDto();
        testProgramResponseDto.setId(programId);
        testProgramResponseDto.setName("New Program");
        testProgramResponseDto.setLearnerId(learnerId);
        testProgramResponseDto.setTrainerId(trainerId);
    }

    // --- Tests para createProgram (POST /api/programs) ---
    @Test
    @WithMockUser(roles = {"ADMIN", "TRAINER"})
    void shouldReturn201CreatedWhenCreateProgramSuccessful() throws Exception {
        /// Arrange
        when(programService.createProgram(any(ProgramRequestDto.class))).thenReturn(testProgramResponseDto);

        /// Act & Assert
        mockMvc.perform(post("/api/programs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testProgramRequestDto))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id", is(testProgramResponseDto.getId().intValue())))
                .andExpect(jsonPath("$.name", is(testProgramResponseDto.getName())));

        /// Verify
        verify(programService).createProgram(any(ProgramRequestDto.class));
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "TRAINER"})
    void shouldReturn400BadRequestWhenCreateProgramDtoIsInvalid() throws Exception {
        /// Arrange
        testProgramRequestDto.setLearnerId(null);

        /// Act & Assert
        mockMvc.perform(post("/api/programs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testProgramRequestDto))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("learnerId")));

        /// Verify
        verify(programService, never()).createProgram(any());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "TRAINER"})
    void shouldReturn409ConflictWhenCreateProgramThrowsDuplicateResource() throws Exception {
        /// Arrange
        String errorMessage = "Program name exists for learner";
        when(programService.createProgram(any(ProgramRequestDto.class)))
                .thenThrow(new DuplicateResourceException(errorMessage));

        /// Act & Assert
        mockMvc.perform(post("/api/programs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testProgramRequestDto))
                        .with(csrf()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", is(errorMessage)));
        /// Verify
        verify(programService).createProgram(any(ProgramRequestDto.class));
    }

    // --- Tests para assignRoutineToProgram (PATCH /api/programs/{id}/assign/{routineId}) ---
    @Test
    @WithMockUser(roles = {"ADMIN", "TRAINER"})
    void shouldReturn200OkWhenAssignRoutineSuccessful() throws Exception {
        /// Arrange
        when(programService.assignRoutineToProgram(programId, routineId)).thenReturn(testProgramResponseDto);

        /// Act & Assert
        mockMvc.perform(patch("/api/programs/{id}/assign/{routineId}", programId, routineId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(programId.intValue())));

        /// Verify
        verify(programService).assignRoutineToProgram(programId, routineId);
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "TRAINER"})
    void shouldReturn400BadRequestWhenAssignRoutineThrowsInvalidOperation() throws Exception {
        /// Arrange
        String errorMessage = "Cannot assign to finished program";
        when(programService.assignRoutineToProgram(programId, routineId))
                .thenThrow(new InvalidOperationException(errorMessage));

        /// Act & Assert
        mockMvc.perform(patch("/api/programs/{id}/assign/{routineId}", programId, routineId)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is(errorMessage)));

        /// Verify
        verify(programService).assignRoutineToProgram(programId, routineId);
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "TRAINER"})
    void shouldReturn404NotFoundWhenAssignRoutineAndProgramNotFound() throws Exception {
        /// Arrange
        Long nonExistentProgramId = 99L;
        String errorMessage = "Program not found";
        when(programService.assignRoutineToProgram(nonExistentProgramId, routineId))
                .thenThrow(new ResourceNotFoundException(errorMessage));

        /// Act & Assert
        mockMvc.perform(patch("/api/programs/{id}/assign/{routineId}", nonExistentProgramId, routineId)
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(errorMessage)));
        /// Verify
        verify(programService).assignRoutineToProgram(nonExistentProgramId, routineId);
    }

    // --- Tests para getProgramsByLearner (GET /api/programs/{learnerId}) ---
    @Test
    @WithMockUser
    void shouldReturn200OkAndPageWhenGetProgramsByLearner() throws Exception {
        /// Arrange
        Page<ProgramResponseDto> programPage = new PageImpl<>(List.of(testProgramResponseDto));
        PageRequest pageRequest = PageRequest.of(0, 5);
        when(programService.getProgramsByLearner(eq(learnerId), any(PageRequest.class))).thenReturn(programPage);

        /// Act & Assert
        mockMvc.perform(get("/api/programs/{learnerId}", learnerId)
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id", is(programId.intValue())))
                .andExpect(jsonPath("$.totalElements", is(1)));

        /// Verify
        verify(programService).getProgramsByLearner(eq(learnerId), eq(pageRequest));
    }

    @Test
    @WithMockUser
    void shouldReturn404NotFoundWhenGetProgramsForNonExistentLearner() throws Exception {
        /// Arrange
        Long nonExistentLearnerId = 99L;
        String errorMessage = "Learner not found";
        PageRequest pageRequest = PageRequest.of(0, 10);
        when(programService.getProgramsByLearner(eq(nonExistentLearnerId), any(PageRequest.class)))
                .thenThrow(new ResourceNotFoundException(errorMessage));

        /// Act & Assert
        mockMvc.perform(get("/api/programs/{learnerId}", nonExistentLearnerId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(errorMessage)));

        /// Verify
        verify(programService).getProgramsByLearner(eq(nonExistentLearnerId), any(PageRequest.class));
    }

    // --- Tests para deleteProgram (DELETE /api/programs/{id}) ---
    @Test
    @WithMockUser(roles = {"ADMIN", "TRAINER"})
    void shouldReturn204NoContentWhenDeleteProgramSuccessful() throws Exception {
        /// Arrange
        doNothing().when(programService).deleteProgram(programId);

        /// Act & Assert
        mockMvc.perform(delete("/api/programs/{id}", programId)
                        .with(csrf()))
                .andExpect(status().isNoContent());
        /// Verify
        verify(programService).deleteProgram(programId);
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "TRAINER"})
    void shouldReturn400BadRequestWhenDeleteProgramThrowsInvalidOperation() throws Exception {
        /// Arrange
        String errorMessage = "Cannot delete active program";
        doThrow(new InvalidOperationException(errorMessage)).when(programService).deleteProgram(programId);

        /// Act & Assert
        mockMvc.perform(delete("/api/programs/{id}", programId)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is(errorMessage)));
        /// Verify
        verify(programService).deleteProgram(programId);
    }
}