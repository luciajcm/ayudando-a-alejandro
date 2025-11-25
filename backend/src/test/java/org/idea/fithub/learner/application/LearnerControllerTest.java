package org.idea.fithub.learner.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.idea.fithub.exceptions.*;
import org.idea.fithub.global.GlobalExceptionHandler;
import org.idea.fithub.learner.domain.Learner;
import org.idea.fithub.learner.domain.LearnerService;
import org.idea.fithub.learner.dto.LearnerRequestDto;
import org.idea.fithub.learner.dto.LearnerResponseDto;
import org.idea.fithub.learner.dto.LearnerStatsDto;
import org.idea.fithub.security.auth.jwt.JwtService;
import org.idea.fithub.trainer.domain.GoalType;
import org.idea.fithub.user.domain.Gender;
import org.idea.fithub.user.domain.Role;
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


@WebMvcTest(LearnerController.class)
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
class LearnerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LearnerService learnerService;
    @MockBean
    private JwtService jwtService;
    @MockBean
    private UserService userService;
    @MockBean
    private ModelMapper modelMapper;

    @Autowired
    private ObjectMapper objectMapper;

    private LearnerRequestDto testLearnerRequestDto;
    private LearnerResponseDto testLearnerResponseDto;
    private LearnerStatsDto testLearnerStatsDto;
    private Long learnerId = 1L;

    @BeforeEach
    void setUp() {
        testLearnerRequestDto = new LearnerRequestDto();
        testLearnerRequestDto.setFirstName("Test");
        testLearnerRequestDto.setLastName("Learner");
        testLearnerRequestDto.setEmail("test@learner.com");
        testLearnerRequestDto.setPassword("Password123!");
        testLearnerRequestDto.setUsername("testlearner");
        testLearnerRequestDto.setGender(Gender.FEMALE);
        testLearnerRequestDto.setPhoneNumber("111222333");
        testLearnerRequestDto.setRole(Role.LEARNER);
        testLearnerRequestDto.setWeight(60.0);
        testLearnerRequestDto.setGoalType(GoalType.MUSCLE_MASS_GAIN);
        testLearnerRequestDto.setHeight(1.75);
        testLearnerResponseDto = new LearnerResponseDto();
        testLearnerResponseDto.setId(learnerId);
        testLearnerResponseDto.setEmail("test@learner.com");

        testLearnerStatsDto = new LearnerStatsDto();
        testLearnerStatsDto.setWeight(65.0);
        testLearnerStatsDto.setHeight(1.70);
        testLearnerStatsDto.setGoalType(GoalType.SHAPE_BODY);

        lenient().when(modelMapper.map(any(Learner.class), eq(LearnerResponseDto.class)))
                .thenReturn(testLearnerResponseDto);
    }

    @Test
    @WithMockUser
    void shouldReturn200OkAndPageWhenGetAllLearners() throws Exception {
        /// Arrange
        Page<LearnerResponseDto> learnerPage = new PageImpl<>(List.of(testLearnerResponseDto));
        when(learnerService.getAllLearners(any(PageRequest.class))).thenReturn(learnerPage);

        /// Act & Assert
        mockMvc.perform(get("/api/learners")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id", is(learnerId.intValue())))
                .andExpect(jsonPath("$.totalElements", is(1)));
        /// Verify
        verify(learnerService).getAllLearners(PageRequest.of(0, 10));
    }

    @Test
    @WithMockUser
    void shouldReturn200OkAndLearnerDtoWhenGetLearnerById() throws Exception {
        /// Arrange
        when(learnerService.getLearner(learnerId)).thenReturn(testLearnerResponseDto);

        /// Act & Assert
        mockMvc.perform(get("/api/learners/{id}", learnerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(learnerId.intValue())))
                .andExpect(jsonPath("$.email", is(testLearnerResponseDto.getEmail())));
        /// Verify
        verify(learnerService).getLearner(learnerId);
    }

    @Test
    @WithMockUser
    void shouldReturn404NotFoundWhenGetLearnerByIdNotFound() throws Exception {
        /// Arrange
        Long nonExistentId = 99L;
        String errorMessage = "Learner not found";
        when(learnerService.getLearner(nonExistentId)).thenThrow(new ResourceNotFoundException(errorMessage));

        /// Act & Assert
        mockMvc.perform(get("/api/learners/{id}", nonExistentId))
                .andExpect(status().isNotFound()) // GHE
                .andExpect(jsonPath("$.message", is(errorMessage)));
        /// Verify
        verify(learnerService).getLearner(nonExistentId);
    }

    @Test
    @WithMockUser(roles="ADMIN")
    void shouldReturn201CreatedWhenCreateLearnerSuccessful() throws Exception {
        /// Arrange
        Learner createdLearnerEntity = new Learner();
        createdLearnerEntity.setId(learnerId);
        when(learnerService.createLearner(any(LearnerRequestDto.class))).thenReturn(createdLearnerEntity);

        /// Act & Assert
        mockMvc.perform(post("/api/learners")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testLearnerRequestDto))
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id", is(testLearnerResponseDto.getId().intValue())))
                .andExpect(jsonPath("$.email", is(testLearnerResponseDto.getEmail())));

        /// Verify
        verify(learnerService).createLearner(any(LearnerRequestDto.class));
        verify(modelMapper).map(createdLearnerEntity, LearnerResponseDto.class);
    }

    @Test
    @WithMockUser(roles="ADMIN") // Añadido rol ADMIN
    void shouldReturn400BadRequestWhenCreateLearnerDtoIsInvalid() throws Exception {
        /// Arrange
        testLearnerRequestDto.setEmail("invalid-email");

        /// Act & Assert
        mockMvc.perform(post("/api/learners")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testLearnerRequestDto))
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("email")));
        /// Verify
        verify(learnerService, never()).createLearner(any());
    }

    @Test
    @WithMockUser(roles="ADMIN")
    void shouldReturn409ConflictWhenCreateLearnerThrowsDuplicateResource() throws Exception {
        /// Arrange
        String errorMessage = "Email already exists";
        when(learnerService.createLearner(any(LearnerRequestDto.class)))
                .thenThrow(new DuplicateResourceException(errorMessage));

        /// Act & Assert
        mockMvc.perform(post("/api/learners")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testLearnerRequestDto))
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().isConflict()) // GHE
                .andExpect(jsonPath("$.message", is(errorMessage)));
        /// Verify
        verify(learnerService).createLearner(any(LearnerRequestDto.class));
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturn204NoContentWhenDeleteLearnerSuccessful() throws Exception {
        /// Arrange
        doNothing().when(learnerService).deleteLearner(learnerId);

        /// Act & Assert
        mockMvc.perform(delete("/api/learners/{id}", learnerId)
                        .with(csrf()))
                .andExpect(status().isNoContent());
        /// Verify
        verify(learnerService).deleteLearner(learnerId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturn409ConflictWhenDeleteLearnerThrowsConflictException() throws Exception {
        /// Arrange
        String errorMessage = "Cannot delete learner with programs";
        doThrow(new ConflictException(errorMessage)).when(learnerService).deleteLearner(learnerId);

        /// Act & Assert
        mockMvc.perform(delete("/api/learners/{id}", learnerId)
                        .with(csrf()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", is(errorMessage)));
        /// Verify
        verify(learnerService).deleteLearner(learnerId);
    }


    @Test
    @WithMockUser
    void shouldReturn200OkWhenUpdateLearnerSuccessful() throws Exception {
        /// Arrange
        when(learnerService.updateLearner(eq(learnerId), any(LearnerRequestDto.class)))
                .thenReturn(testLearnerResponseDto);

        /// Act & Assert
        mockMvc.perform(put("/api/learners/{id}", learnerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testLearnerRequestDto))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(learnerId.intValue())))
                .andExpect(jsonPath("$.email", is(testLearnerResponseDto.getEmail())));
        /// Verify
        verify(learnerService).updateLearner(eq(learnerId), any(LearnerRequestDto.class));
    }

    @Test
    @WithMockUser
    void shouldReturn400BadRequestWhenUpdateLearnerDtoIsInvalid() throws Exception {
        /// Arrange
        testLearnerRequestDto.setEmail("invalid");

        /// Act & Assert
        mockMvc.perform(put("/api/learners/{id}", learnerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testLearnerRequestDto))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("email")));
        /// Verify
        verify(learnerService, never()).updateLearner(anyLong(), any());
    }


    @Test
    @WithMockUser
    void shouldReturn200OkWhenUpdateStatsSuccessful() throws Exception {
        /// Arrange
        when(learnerService.updateLearnerStats(eq(learnerId), any(LearnerStatsDto.class)))
                .thenReturn(testLearnerResponseDto);

        /// Act & Assert
        mockMvc.perform(patch("/api/learners/{id}/stats", learnerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testLearnerStatsDto))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(learnerId.intValue())));
        /// Verify
        verify(learnerService).updateLearnerStats(eq(learnerId), any(LearnerStatsDto.class));
    }

    @Test
    @WithMockUser
    void shouldReturn400BadRequestWhenUpdateStatsDtoIsInvalid() throws Exception {
        /// Arrange
        testLearnerStatsDto.setWeight(0.0); // Dato inválido

        when(learnerService.updateLearnerStats(eq(learnerId), any(LearnerStatsDto.class)))
                .thenThrow(new BadRequestException("Weight must be greater than 0"));

        /// Act & Assert
        mockMvc.perform(patch("/api/learners/{id}/stats", learnerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testLearnerStatsDto))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Weight")));
        /// Verify
        // Verifica que el servicio FUE llamado (si la validación está ahí)
        verify(learnerService).updateLearnerStats(eq(learnerId), any(LearnerStatsDto.class));
    }

    @Test
    @WithMockUser
    void shouldReturn404NotFoundWhenUpdateStatsForNonExistentLearner() throws Exception {
        /// Arrange
        Long nonExistentId = 99L;
        String errorMessage = "Learner not found";
        when(learnerService.updateLearnerStats(eq(nonExistentId), any(LearnerStatsDto.class)))
                .thenThrow(new ResourceNotFoundException(errorMessage));

        /// Act & Assert
        mockMvc.perform(patch("/api/learners/{id}/stats", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testLearnerStatsDto))
                        .with(csrf()))
                .andExpect(status().isNotFound()) // GHE
                .andExpect(jsonPath("$.message", is(errorMessage)));
        /// Verify
        verify(learnerService).updateLearnerStats(eq(nonExistentId), any(LearnerStatsDto.class));
    }
}