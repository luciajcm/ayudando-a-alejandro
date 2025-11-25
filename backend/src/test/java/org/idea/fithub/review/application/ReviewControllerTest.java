package org.idea.fithub.review.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.idea.fithub.exceptions.*;
import org.idea.fithub.global.GlobalExceptionHandler;
import org.idea.fithub.review.domain.Review;
import org.idea.fithub.review.domain.ReviewService;
import org.idea.fithub.review.dto.ReviewRequestDto;
import org.idea.fithub.review.dto.ReviewResponseDto;
import org.idea.fithub.review.dto.TrainerReviewResponseDto;
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


import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.containsString;


@WebMvcTest(ReviewController.class)
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReviewService reviewService;
    @MockBean
    private JwtService jwtService;
    @MockBean
    private UserService userService;
    @MockBean
    private ModelMapper modelMapper;

    @Autowired
    private ObjectMapper objectMapper;

    // --- Datos de Prueba ---
    private ReviewRequestDto testReviewRequestDto;
    private ReviewResponseDto testReviewResponseDto;
    private Page<TrainerReviewResponseDto> testTrainerReviewPage;
    private Long trainerId = 10L;
    private Long programId = 1L;
    private Long reviewId = 1000L;


    @BeforeEach
    void setUp() {
        testReviewRequestDto = new ReviewRequestDto();
        testReviewRequestDto.setProgramId(programId);
        testReviewRequestDto.setTargetId(trainerId);
        testReviewRequestDto.setRating(5);
        testReviewRequestDto.setComment("Valid comment");

        testReviewResponseDto = new ReviewResponseDto();
        testReviewResponseDto.setId(reviewId);
        testReviewResponseDto.setRating(5);
        testReviewResponseDto.setComment("Valid comment");

        testTrainerReviewPage = new PageImpl<>(Collections.emptyList());

        lenient().when(modelMapper.map(any(Review.class), eq(ReviewResponseDto.class)))
                .thenReturn(testReviewResponseDto);
    }

    // --- Tests para getReviewsByTrainer (GET /api/reviews/{trainerId}) ---
    @Test
    @WithMockUser
    void shouldReturn200OkAndPageWhenGetReviewsByTrainer() throws Exception {
        /// Arrange
        when(reviewService.getReviewsByTrainer(eq(trainerId), any(PageRequest.class)))
                .thenReturn(testTrainerReviewPage);

        /// Act & Assert
        mockMvc.perform(get("/api/reviews/{trainerId}", trainerId)
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalElements", is(0)));

        /// Verify
        verify(reviewService).getReviewsByTrainer(eq(trainerId), eq(PageRequest.of(0, 5)));
    }

    @Test
    @WithMockUser
    void shouldReturn404NotFoundWhenGetReviewsForNonExistentTrainer() throws Exception {
        /// Arrange
        Long nonExistentId = 99L;
        String errorMessage = "Trainer not found";
        when(reviewService.getReviewsByTrainer(eq(nonExistentId), any(PageRequest.class)))
                .thenThrow(new ResourceNotFoundException(errorMessage));

        /// Act & Assert
        mockMvc.perform(get("/api/reviews/{trainerId}", nonExistentId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(errorMessage)));
        /// Verify
        verify(reviewService).getReviewsByTrainer(eq(nonExistentId), any(PageRequest.class));
    }


    // --- Tests para createReview (POST /api/reviews) ---
    @Test
    @WithMockUser(roles = "LEARNER")
    void shouldReturn201CreatedWhenCreateReviewSuccessful() throws Exception {
        /// Arrange
        Review createdReviewEntity = new Review();
        createdReviewEntity.setId(reviewId);
        when(reviewService.createReview(any(ReviewRequestDto.class))).thenReturn(createdReviewEntity);

        /// Act & Assert
        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testReviewRequestDto))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id", is(testReviewResponseDto.getId().intValue())))
                .andExpect(jsonPath("$.comment", is(testReviewResponseDto.getComment())));

        /// Verify
        verify(reviewService).createReview(any(ReviewRequestDto.class));
        verify(modelMapper).map(createdReviewEntity, ReviewResponseDto.class);
    }

    @Test
    @WithMockUser(roles = "LEARNER")
    void shouldReturn400BadRequestWhenCreateReviewDtoIsInvalid() throws Exception {
        /// Arrange
        testReviewRequestDto.setRating(10);
        testReviewRequestDto.setComment(null);

        /// Act & Assert
        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testReviewRequestDto))
                        .with(csrf()))
                .andExpect(status().isBadRequest()) // @Valid
                .andExpect(jsonPath("$.message", containsString("rating")))
                .andExpect(jsonPath("$.message", containsString("comment")));
        /// Verify
        verify(reviewService, never()).createReview(any());
    }

    @Test
    @WithMockUser(roles = "LEARNER")
    void shouldReturn409ConflictWhenCreateReviewThrowsDuplicateResource() throws Exception {
        /// Arrange
        String errorMessage = "Review already exists";
        when(reviewService.createReview(any(ReviewRequestDto.class)))
                .thenThrow(new DuplicateResourceException(errorMessage));

        /// Act & Assert
        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testReviewRequestDto))
                        .with(csrf()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", is(errorMessage)));
        /// Verify
        verify(reviewService).createReview(any(ReviewRequestDto.class));
    }

    @Test
    @WithMockUser(roles = "LEARNER")
    void shouldReturn400BadRequestWhenCreateReviewThrowsInvalidOperation() throws Exception {
        /// Arrange
        String errorMessage = "Program has not ended";
        when(reviewService.createReview(any(ReviewRequestDto.class)))
                .thenThrow(new InvalidOperationException(errorMessage));

        /// Act & Assert
        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testReviewRequestDto))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is(errorMessage)));
        /// Verify
        verify(reviewService).createReview(any(ReviewRequestDto.class));
    }


    // --- Tests para deleteReview (DELETE /api/reviews/{id}) ---
    @Test
    @WithMockUser(roles = {"ADMIN", "LEARNER"})
    void shouldReturn204NoContentWhenDeleteReviewSuccessful() throws Exception {
        /// Arrange
        doNothing().when(reviewService).deleteReview(reviewId);

        /// Act & Assert
        mockMvc.perform(delete("/api/reviews/{id}", reviewId)
                        .with(csrf()))
                .andExpect(status().isNoContent());
        /// Verify
        verify(reviewService).deleteReview(reviewId);
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "LEARNER"})
    void shouldReturn404NotFoundWhenDeleteReviewNotFound() throws Exception {
        /// Arrange
        Long nonExistentId = 9999L;
        String errorMessage = "Review not found";
        doThrow(new ResourceNotFoundException(errorMessage)).when(reviewService).deleteReview(nonExistentId);

        /// Act & Assert
        mockMvc.perform(delete("/api/reviews/{id}", nonExistentId)
                        .with(csrf()))
                .andExpect(status().isNotFound()) // GHE
                .andExpect(jsonPath("$.message", is(errorMessage)));
        /// Verify
        verify(reviewService).deleteReview(nonExistentId);
    }
}