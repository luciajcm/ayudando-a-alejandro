package org.idea.fithub.review.infrastructure;

import org.idea.fithub.BaseRepositoryTest;
import org.idea.fithub.learner.domain.Learner;
import org.idea.fithub.program.domain.Program;
import org.idea.fithub.review.domain.Review;
import org.idea.fithub.review.dto.TrainerReviewResponseDto;
import org.idea.fithub.trainer.domain.Trainer;
import org.idea.fithub.user.domain.Gender;
import org.idea.fithub.user.domain.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;

class ReviewRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ReviewRepository reviewRepository;

    private Learner createTestLearner(String email, String username, String phone) {
        Learner learner = new Learner();
        learner.setEmail(email);
        learner.setPhoneNumber(phone);
        learner.setUsername(username);
        learner.setFirstName("Test");
        learner.setLastName("Learner");
        learner.setPassword("password");
        learner.setRole(Role.LEARNER);
        learner.setGender(Gender.MALE);
        learner.setHeight(175.0);
        learner.setWeight(70.0);
        return entityManager.persist(learner);
    }

    private Trainer createTestTrainer(String email, String username, String phone) {
        Trainer trainer = new Trainer();
        trainer.setEmail(email);
        trainer.setPhoneNumber(phone);
        trainer.setUsername(username);
        trainer.setFirstName("Test");
        trainer.setLastName("Trainer");
        trainer.setPassword("password");
        trainer.setRole(Role.TRAINER);
        trainer.setHeight(165.0);
        trainer.setWeight(60.0);
        trainer.setGender(Gender.FEMALE);
        return entityManager.persist(trainer);
    }

    private Program createTestProgram(Learner learner, Trainer trainer) {
        Program program = new Program();
        program.setName("Test Program");
        program.setLearner(learner);
        program.setTrainer(trainer);
        return entityManager.persist(program);
    }

    // --- Tests ---

    /**
     * Prueba la consulta personalizada: existsByProgramId
     * Caso: Happy Path (Una review existe para ese ID de programa)
     */
    @Test
    void shouldReturnTrueWhenReviewExistsByProgramId() {
        /// Arrange
        Learner learner = createTestLearner("author@test.com", "author", "111111");
        Trainer trainer = createTestTrainer("target@test.com", "target", "222222");
        Program program = createTestProgram(learner, trainer);

        Review review = new Review();
        review.setRating(5);
        review.setComment("Great program!");
        review.setAuthor(learner);
        review.setTarget(trainer);
        review.setProgram(program);

        entityManager.persistAndFlush(review);

        /// Act
        boolean exists = reviewRepository.existsByProgramId(program.getId());

        /// Assert
        assertThat(exists).isTrue();
    }

    /**
     * Prueba la consulta personalizada: existsByProgramId
     * Caso: Negativo (No existe ninguna review para ese ID de programa)
     */
    @Test
    void shouldReturnFalseWhenReviewDoesNotExistByProgramId() {
        /// Arrange
        Learner learner = createTestLearner("author2@test.com", "author2", "333333");
        Trainer trainer = createTestTrainer("target2@test.com", "target2", "444444");
        Program unreviewedProgram = createTestProgram(learner, trainer);

        /// Act
        boolean exists = reviewRepository.existsByProgramId(unreviewedProgram.getId());

        /// Assert
        assertThat(exists).isFalse();
    }

    /**
     * Prueba la consulta personalizada: existsByProgramId
     * Caso: Edge Case (Probamos con un ID que ni siquiera existe)
     */
    @Test
    void shouldReturnFalseWhenProgramIdDoesNotExistAtAll() {
        /// Arrange
        Long nonExistentProgramId = 9999L;

        /// Act
        boolean exists = reviewRepository.existsByProgramId(nonExistentProgramId);

        /// Assert
        assertThat(exists).isFalse();
    }

    //--- Pruebas para findByTargetId (Proyección DTO) ---

    @Test
    void shouldReturnPagedReviewsForTargetTrainer() {
        /// Arrange
        Learner learner1 = createTestLearner("l1@test.com", "l1", "100001");
        Learner learner2 = createTestLearner("l2@test.com", "l2", "100002");
        Trainer targetTrainer = createTestTrainer("target-pro@test.com", "target-pro", "100003");
        Trainer otherTrainer = createTestTrainer("other@test.com", "other", "100004");

        Program program1 = createTestProgram(learner1, targetTrainer);
        Program program2 = createTestProgram(learner2, targetTrainer);
        Program program3 = createTestProgram(learner1, otherTrainer);

        Review review1 = new Review();
        review1.setRating(5);
        review1.setAuthor(learner1);
        review1.setTarget(targetTrainer);
        review1.setProgram(program1);
        review1.setComment("¡Genial!");

        Review review2 = new Review();
        review2.setRating(3);
        review2.setAuthor(learner2);
        review2.setTarget(targetTrainer);
        review2.setProgram(program2);
        review2.setComment("OK.");

        Review reviewForOther = new Review();
        reviewForOther.setRating(1);
        reviewForOther.setAuthor(learner1);
        reviewForOther.setTarget(otherTrainer);
        reviewForOther.setProgram(program3);
        reviewForOther.setComment("Mal.");

        entityManager.persist(review1);
        entityManager.persist(review2);
        entityManager.persist(reviewForOther);
        entityManager.flush();

        Pageable pageable = PageRequest.of(0, 1);

        /// Act
        Page<TrainerReviewResponseDto> reviewPage = reviewRepository.findByTargetId(targetTrainer.getId(), pageable);

        /// Assert
        assertThat(reviewPage).isNotNull();
        assertThat(reviewPage.getTotalElements()).isEqualTo(2);
        assertThat(reviewPage.getTotalPages()).isEqualTo(2);
        assertThat(reviewPage.getNumber()).isEqualTo(0);
        assertThat(reviewPage.getContent()).hasSize(1);

        TrainerReviewResponseDto dto = reviewPage.getContent().getFirst();

        assertThat(dto.getId()).isNotNull();
        assertThat(dto.getRating()).isNotNull();
        assertThat(dto.getComment()).isNotNull();
    }

    /**
     * Prueba la consulta personalizada: findByTargetId
     * Caso: Edge Case (El Trainer existe pero no tiene reviews)
     */
    @Test
    void shouldReturnEmptyPageWhenTrainerHasNoReviews() {
        /// Arrange
        Trainer trainerWithNoReviews = createTestTrainer("lonely@test.com", "lonely", "100005");
        Pageable pageable = PageRequest.of(0, 10);

        /// Act
        Page<TrainerReviewResponseDto> reviewPage = reviewRepository.findByTargetId(trainerWithNoReviews.getId(), pageable);

        /// Assert
        assertThat(reviewPage).isNotNull();
        assertThat(reviewPage.getTotalElements()).isZero();
        assertThat(reviewPage.getContent()).isEmpty();
    }

    /**
     * Prueba la consulta personalizada: findByTargetId
     * Caso: Edge Case (El Trainer ID no existe)
     */
    @Test
    void shouldReturnEmptyPageWhenTrainerIdDoesNotExist() {
        /// Arrange
        Long nonExistentTrainerId = 9999L;
        Pageable pageable = PageRequest.of(0, 10);

        /// Act
        Page<TrainerReviewResponseDto> reviewPage = reviewRepository.findByTargetId(nonExistentTrainerId, pageable);

        /// Assert
        assertThat(reviewPage).isNotNull();
        assertThat(reviewPage.getTotalElements()).isZero();
        assertThat(reviewPage.getContent()).isEmpty();
    }
}