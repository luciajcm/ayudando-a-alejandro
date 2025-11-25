package org.idea.fithub.trainingCheck.infrastructure;

import org.idea.fithub.BaseRepositoryTest;
import org.idea.fithub.learner.domain.Learner;
import org.idea.fithub.trainingCheck.domain.TrainingCheck;
import org.idea.fithub.user.domain.Gender;
import org.idea.fithub.user.domain.Role;
import org.idea.fithub.user.domain.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class TrainingCheckRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TrainingCheckRepository trainingCheckRepository;

    private Learner learner1;
    private Learner learner2;
    private TrainingCheck checkLearner1;
    private LocalDate today;
    private LocalDate yesterday;

    private Learner createTestLearner(String email, String username, String phone) {
        Learner learner = Learner.builder()
                .email(email)
                .username(username)
                .phoneNumber(phone)
                .firstName("Test")
                .lastName("Learner")
                .password("password123!")
                .role(Role.LEARNER)
                .gender(Gender.MALE)
                .weight(75.0)
                .height(175.0)
                .userStatus(UserStatus.AVAILABLE)
                .build();
        return entityManager.persist(learner);
    }

    @BeforeEach
    void setUp() {
        // Arrange global
        learner1 = createTestLearner("learner1@test.com", "learner1", "111111");
        learner2 = createTestLearner("learner2@test.com", "learner2", "222222");

        today = LocalDate.now();
        yesterday = LocalDate.now().minusDays(1);

        checkLearner1 = new TrainingCheck(today, learner1);
        TrainingCheck checkLearner1Old = new TrainingCheck(yesterday, learner1);
        TrainingCheck checkLearner2 = new TrainingCheck(today, learner2);

        entityManager.persist(checkLearner1);
        entityManager.persist(checkLearner1Old);
        entityManager.persist(checkLearner2);
        entityManager.flush();
    }

    // --- Pruebas para existsByLearnerAndDate y findByLearnerAndDate ---

    @Test
    void shouldReturnTrueWhenCheckExistsByLearnerAndDate() {
        // Act
        boolean exists = trainingCheckRepository.existsByLearnerAndDate(learner1, today);

        // Assert
        assertThat(exists).isTrue();
    }

    @Test
    void shouldFindCheckByLearnerAndDateWhenExists() {
        // Act
        Optional<TrainingCheck> found = trainingCheckRepository.findByLearnerAndDate(learner1, today);

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getLearner().getId()).isEqualTo(learner1.getId());
        assertThat(found.get().getDate()).isEqualTo(today);
    }

    @Test
    void shouldReturnFalseWhenCheckDoesNotExistForDate() {
        // Arrange
        LocalDate futureDate = today.plusDays(1);

        // Act
        boolean exists = trainingCheckRepository.existsByLearnerAndDate(learner1, futureDate);
        Optional<TrainingCheck> found = trainingCheckRepository.findByLearnerAndDate(learner1, futureDate);

        // Assert
        assertThat(exists).isFalse();
        assertThat(found).isNotPresent();
    }

    @Test
    void shouldReturnFalseWhenCheckExistsForDateButDifferentLearner() {
        // Act
        boolean exists = trainingCheckRepository.existsByLearnerAndDate(learner2, yesterday);
        Optional<TrainingCheck> found = trainingCheckRepository.findByLearnerAndDate(learner2, yesterday);

        // Assert
        assertThat(exists).isFalse();
        assertThat(found).isNotPresent();
    }

    // --- Pruebas para findByLearner y countByLearner ---

    @Test
    void shouldReturnCorrectListOfChecksForLearner() {
        // Act
        List<TrainingCheck> checks = trainingCheckRepository.findByLearner(learner1);

        // Assert
        assertThat(checks).isNotNull();
        assertThat(checks).hasSize(2);
        assertThat(checks).allMatch(check -> check.getLearner().getId().equals(learner1.getId()));
    }

    @Test
    void shouldReturnCorrectCountForLearner() {
        // Act
        Integer count = trainingCheckRepository.countByLearner(learner1);

        // Assert
        assertThat(count).isEqualTo(2);
    }

    @Test
    void shouldReturnEmptyListAndZeroCountWhenLearnerHasNoChecks() {
        // Arrange
        Learner learner3 = createTestLearner("learner3@test.com", "learner3", "333333");

        // Act
        List<TrainingCheck> checks = trainingCheckRepository.findByLearner(learner3);
        Integer count = trainingCheckRepository.countByLearner(learner3);

        // Assert
        assertThat(checks).isNotNull().isEmpty();
        assertThat(count).isZero();
    }

    // --- Pruebas adicionales para mayor cobertura ---

    @Test
    void shouldFindMultipleChecksForSameLearnerDifferentDates() {
        // Act
        List<TrainingCheck> checks = trainingCheckRepository.findByLearner(learner1);

        // Assert
        assertThat(checks).hasSize(2);
        assertThat(checks).extracting(TrainingCheck::getDate)
                .containsExactlyInAnyOrder(today, yesterday);
    }

    @Test
    void shouldReturnDifferentCountsForDifferentLearners() {
        // Act
        Integer countLearner1 = trainingCheckRepository.countByLearner(learner1);
        Integer countLearner2 = trainingCheckRepository.countByLearner(learner2);

        // Assert
        assertThat(countLearner1).isEqualTo(2);
        assertThat(countLearner2).isEqualTo(1);
    }

    @Test
    void shouldNotFindCheckAfterDeletion() {
        // Arrange
        TrainingCheck checkToDelete = trainingCheckRepository.findByLearnerAndDate(learner1, today)
                .orElseThrow();

        // Act
        trainingCheckRepository.delete(checkToDelete);
        entityManager.flush();

        Optional<TrainingCheck> foundAfterDeletion = trainingCheckRepository.findByLearnerAndDate(learner1, today);

        // Assert
        assertThat(foundAfterDeletion).isNotPresent();
    }
}