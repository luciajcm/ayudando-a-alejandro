package org.idea.fithub.exercise.infrastructure;

import org.idea.fithub.BaseRepositoryTest;
import org.idea.fithub.exercise.domain.Exercise;
import org.idea.fithub.exercise.domain.Muscle;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class ExerciseRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ExerciseRepository exerciseRepository;


    private Exercise createTestExercise(String name, Muscle muscle) {
        Exercise exercise = new Exercise();
        exercise.setName(name);
        exercise.setMuscle(muscle);
        exercise.setDescription("Test Description");
        return entityManager.persistAndFlush(exercise);
    }

    @Test
    void shouldFindExerciseByNameWhenNameExists() {
        /// Arrange
        Exercise expectedExercise = createTestExercise("Bench Press", Muscle.CHEST);

        /// Act
        Optional<Exercise> found = exerciseRepository.findByName("Bench Press");

        /// Assert
        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(expectedExercise.getId());
        assertThat(found.get().getName()).isEqualTo("Bench Press");
    }

    @Test
    void shouldReturnEmptyOptionalWhenNameDoesNotExist() {
        /// Arrange

        /// Act
        Optional<Exercise> found = exerciseRepository.findByName("Non Existent Exercise");

        /// Assert
        assertThat(found).isNotPresent();
    }

    @Test
    void shouldReturnEmptyOptionalWhenNameMatchesButCaseIsDifferent() {
        /// Arrange
        createTestExercise("Squat", Muscle.LEGS); // Persistido con 'S' mayúscula

        /// Act
        Optional<Exercise> found = exerciseRepository.findByName("squat");

        /// Assert
        assertThat(found).isNotPresent();
    }


    @Test
    void shouldFindCorrectExerciseWhenMultipleExercisesExist() {
        /// Arrange
        createTestExercise("Deadlift", Muscle.BACK);
        Exercise expectedExercise = createTestExercise("Overhead Press", Muscle.SHOULDERS);
        createTestExercise("Bicep Curl", Muscle.ARMS);

        /// Act
        Optional<Exercise> found = exerciseRepository.findByName("Overhead Press");

        /// Assert
        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(expectedExercise.getId());
        assertThat(found.get().getMuscle()).isEqualTo(Muscle.SHOULDERS);
    }
}