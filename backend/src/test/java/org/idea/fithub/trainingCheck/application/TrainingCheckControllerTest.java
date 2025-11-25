package org.idea.fithub.trainingCheck.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.idea.fithub.exceptions.BadRequestException;
import org.idea.fithub.exceptions.DuplicateResourceException;
import org.idea.fithub.exceptions.ResourceNotFoundException;
import org.idea.fithub.global.GlobalExceptionHandler;
import org.idea.fithub.trainingCheck.domain.TrainingCheckService;
import org.idea.fithub.trainingCheck.dto.TrainingCheckResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.idea.fithub.security.auth.jwt.JwtService;
import org.idea.fithub.user.domain.UserService;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.containsString;

@WebMvcTest(TrainingCheckController.class)
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
class TrainingCheckControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TrainingCheckService trainingCheckService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private Long learnerId = 1L;
    private LocalDate testDate;
    private String testDateString;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @BeforeEach
    void setUp() {
        /// Arrange global
        testDate = LocalDate.now().minusDays(1);
        testDateString = testDate.format(formatter);
    }

    // --- Tests para markCheck (POST /{learnerId}/check) ---

    @Test
    void shouldReturn201CreatedWhenMarkCheckIsSuccessful() throws Exception {
        /// Arrange
        doNothing().when(trainingCheckService).markCheck(learnerId, testDate);

        /// Act & Assert
        mockMvc.perform(post("/api/learners/{learnerId}/check", learnerId)
                        .param("date", testDateString)
                        .with(user("testuser").roles("LEARNER"))
                        .with(csrf()))
                .andExpect(status().isCreated());

        /// Verify
        verify(trainingCheckService).markCheck(learnerId, testDate);
    }

    @Test
    void shouldReturn409ConflictWhenMarkCheckThrowsDuplicateResourceException() throws Exception {
        /// Arrange
        String errorMessage = "Learner has already marked attendance for this day.";
        doThrow(new DuplicateResourceException(errorMessage))
                .when(trainingCheckService).markCheck(learnerId, testDate);

        /// Act & Assert
        mockMvc.perform(post("/api/learners/{learnerId}/check", learnerId)
                        .param("date", testDateString)
                        .with(user("testuser").roles("LEARNER"))
                        .with(csrf()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(errorMessage))
                .andExpect(jsonPath("$.path").value("/api/learners/" + learnerId + "/check"));

        /// Verify
        verify(trainingCheckService).markCheck(learnerId, testDate);
    }

    @Test
    void shouldReturn400BadRequestWhenMarkCheckThrowsBadRequestException() throws Exception {
        /// Arrange
        String errorMessage = "Cannot mark attendance for a future date";
        doThrow(new BadRequestException(errorMessage))
                .when(trainingCheckService).markCheck(learnerId, testDate);

        /// Act & Assert
        mockMvc.perform(post("/api/learners/{learnerId}/check", learnerId)
                        .param("date", testDateString)
                        .with(user("testuser").roles("LEARNER"))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(errorMessage));

        /// Verify
        verify(trainingCheckService).markCheck(learnerId, testDate);
    }

    @Test
    void shouldReturn400BadRequestWhenDateParameterIsMissing() throws Exception {
        /// Act & Assert
        mockMvc.perform(post("/api/learners/{learnerId}/check", learnerId)
                        // SIN .param("date", ...)
                        .with(user("testuser").roles("LEARNER"))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("parameter 'date' is missing")));

        /// Verify
        verify(trainingCheckService, never()).markCheck(anyLong(), any(LocalDate.class));
    }

    @Test
    void shouldReturn400BadRequestWhenDateFormatIsInvalid() throws Exception {
        /// Arrange
        String invalidDateString = "esto-no-es-una-fecha";

        /// Act & Assert
        mockMvc.perform(post("/api/learners/{learnerId}/check", learnerId)
                        .param("date", invalidDateString)
                        .with(user("testuser").roles("LEARNER"))
                        .with(csrf()))
                .andExpect(status().isBadRequest());

        /// Verify
        verify(trainingCheckService, never()).markCheck(anyLong(), any(LocalDate.class));
    }


    // --- Tests para unmarkCheck (DELETE /{learnerId}/check) ---

    @Test
    void shouldReturn204NoContentWhenUnmarkCheckIsSuccessful() throws Exception {
        /// Arrange
        doNothing().when(trainingCheckService).unmarkCheck(learnerId, testDate);

        /// Act & Assert
        mockMvc.perform(delete("/api/learners/{learnerId}/check", learnerId)
                        .param("date", testDateString)
                        .with(user("testuser").roles("LEARNER"))
                        .with(csrf()))
                .andExpect(status().isNoContent());

        /// Verify
        verify(trainingCheckService).unmarkCheck(learnerId, testDate);
    }

    @Test
    void shouldReturn404NotFoundWhenUnmarkCheckThrowsResourceNotFoundException() throws Exception {
        /// Arrange
        String errorMessage = "There is no register for that day";
        doThrow(new ResourceNotFoundException(errorMessage))
                .when(trainingCheckService).unmarkCheck(learnerId, testDate);

        /// Act & Assert
        mockMvc.perform(delete("/api/learners/{learnerId}/check", learnerId)
                        .param("date", testDateString)
                        .with(user("testuser").roles("LEARNER"))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(errorMessage));

        /// Verify
        verify(trainingCheckService).unmarkCheck(learnerId, testDate);
    }

    // --- Tests para getTrainingCheck (GET /{learnerId}/checks) ---

    @Test
    void shouldReturn200OkWithDtoWhenGetTrainingCheckIsSuccessful() throws Exception {
        /// Arrange
        TrainingCheckResponseDto responseDto = new TrainingCheckResponseDto(
                learnerId, 5, "0 months and 10 days", Collections.emptyList());
        when(trainingCheckService.getTrainingCheck(learnerId)).thenReturn(responseDto);

        /// Act & Assert
        mockMvc.perform(get("/api/learners/{learnerId}/checks", learnerId)
                        .with(user("testuser").roles("LEARNER")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.learnerId").value(learnerId))
                .andExpect(jsonPath("$.totalChecks").value(5))
                .andExpect(jsonPath("$.duration").value("0 months and 10 days"));

        /// Verify
        verify(trainingCheckService).getTrainingCheck(learnerId);
    }

    @Test
    void shouldReturn404NotFoundWhenGetTrainingCheckThrowsResourceNotFoundException() throws Exception {
        /// Arrange
        Long nonExistentId = 99L;
        String errorMessage = "Learner not found with id: " + nonExistentId;
        when(trainingCheckService.getTrainingCheck(nonExistentId))
                .thenThrow(new ResourceNotFoundException(errorMessage));

        /// Act & Assert
        mockMvc.perform(get("/api/learners/{learnerId}/checks", nonExistentId)
                        .with(user("testuser").roles("LEARNER")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(errorMessage));

        /// Verify
        verify(trainingCheckService).getTrainingCheck(nonExistentId);
    }
}