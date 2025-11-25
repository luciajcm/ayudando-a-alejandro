package org.idea.fithub.trainer.infrastructure;

import org.idea.fithub.BaseRepositoryTest;
import org.idea.fithub.learner.domain.Learner;
import org.idea.fithub.trainer.domain.Trainer;
import org.idea.fithub.user.domain.Gender;
import org.idea.fithub.user.domain.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

class TrainerRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TrainerRepository trainerRepository;

    // --- Helpers para crear las entidades ---

    private Learner createTestLearner(String email, String username, String phone) {
        Learner learner = new Learner();
        learner.setEmail(email);
        learner.setUsername(username); // Añadido: campo obligatorio
        learner.setPhoneNumber(phone);
        learner.setFirstName("Test");
        learner.setLastName("Learner");
        learner.setPassword("password");
        learner.setRole(Role.LEARNER);
        learner.setGender(Gender.MALE);
        learner.setHeight(175.0); // Añadido: campo obligatorio
        learner.setWeight(70.0);
        return entityManager.persistAndFlush(learner);
    }

    private Trainer createTestTrainer(String email, String username, String phone) {
        Trainer trainer = new Trainer();
        trainer.setEmail(email);
        trainer.setUsername(username); // Añadido: campo obligatorio
        trainer.setPhoneNumber(phone);
        trainer.setFirstName("Test");
        trainer.setLastName("Trainer");
        trainer.setPassword("password");
        trainer.setRole(Role.TRAINER);
        trainer.setGender(Gender.FEMALE);
        trainer.setHeight(165.0); // Añadido: campo obligatorio
        trainer.setWeight(60.0);  // Añadido: campo obligatorio
        return entityManager.persistAndFlush(trainer);
    }

    @Test
    void shouldReturnTrueWhenTrainerExistsWithEmail() {
        /// Arrange
        createTestTrainer("trainer@test.com", "trainer_user", "123456");

        /// Act
        boolean exists = trainerRepository.existsByEmail("trainer@test.com");

        /// Assert
        assertThat(exists).isTrue();
    }

    /**
     * Prueba la consulta personalizada: existsByEmail
     * Caso: Negativo (Ningún User existe con ese email)
     */
    @Test
    void shouldReturnFalseWhenEmailDoesNotExist() {
        /// Arrange
        // No persistimos ningún usuario con este email

        /// Act
        boolean exists = trainerRepository.existsByEmail("nonexistent@test.com");

        /// Assert
        assertThat(exists).isFalse();
    }

    /**
     * Prueba la consulta personalizada: existsByEmail
     * Caso: Edge Case (Prueba de Herencia JOINED)
     * Un User SÍ existe con ese email, pero es un LEARNER, no un TRAINER.
     */
    @Test
    void shouldReturnFalseWhenUserExistsWithEmailButIsNotATrainer() {
        /// Arrange
        createTestLearner("learner@test.com", "learner_user", "987654");

        /// Act
        boolean exists = trainerRepository.existsByEmail("learner@test.com");

        /// Assert
        assertThat(exists).isFalse();
    }
}