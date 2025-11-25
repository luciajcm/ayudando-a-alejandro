package org.idea.fithub.learner.infrastructure;

import org.idea.fithub.BaseRepositoryTest;
import org.idea.fithub.learner.domain.Learner;
import org.idea.fithub.trainer.domain.Trainer; // Necesitamos esto para el test de herencia
import org.idea.fithub.user.domain.Gender;
import org.idea.fithub.user.domain.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pruebas de integración para LearnerRepository.
 * - Prueba la consulta personalizada 'existsByEmail'.
 * - Prueba el caso de borde de Herencia (JOINED):
 * Asegura que 'existsByEmail' solo encuentre Learners, no otros Users (Trainers).
 */
class LearnerRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private LearnerRepository learnerRepository;

    // --- Helpers para crear las entidades ---

    private Learner createTestLearner(String email, String username, String phone) {
        Learner learner = new Learner();
        learner.setEmail(email);
        learner.setUsername(username);
        learner.setPhoneNumber(phone);
        learner.setFirstName("Test");
        learner.setLastName("Learner");
        learner.setPassword("password");
        learner.setRole(Role.LEARNER);
        learner.setGender(Gender.MALE);
        learner.setWeight(70.0);
        learner.setHeight(1.75);
        return entityManager.persistAndFlush(learner);
    }

    private Trainer createTestTrainer(String email, String username, String phone) {
        Trainer trainer = new Trainer();
        trainer.setEmail(email);
        trainer.setUsername(username);
        trainer.setPhoneNumber(phone);
        trainer.setFirstName("Test");
        trainer.setLastName("Trainer");
        trainer.setPassword("password");
        trainer.setRole(Role.TRAINER);
        trainer.setGender(Gender.FEMALE);
        trainer.setWeight(80.0);
        trainer.setHeight(1.80);
        return entityManager.persistAndFlush(trainer);
    }

    /**
     * Prueba la consulta personalizada: existsByEmail
     * Caso: Happy Path (Un Learner existe con ese email)
     */
    @Test
    void shouldReturnTrueWhenLearnerExistsWithEmail() {
        /// Arrange
        createTestLearner("learner@test.com", "learner_user", "123456");

        /// Act
        boolean exists = learnerRepository.existsByEmail("learner@test.com");

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
        boolean exists = learnerRepository.existsByEmail("nonexistent@test.com");

        /// Assert
        assertThat(exists).isFalse();
    }

    /**
     * Prueba la consulta personalizada: existsByEmail
     * Caso: Edge Case (Prueba de Herencia JOINED)
     * Un User SÍ existe con ese email, pero es un TRAINER, no un LEARNER.
     */
    @Test
    void shouldReturnFalseWhenUserExistsWithEmailButIsNotALearner() {
        /// Arrange
        // Creamos un Trainer con el email que vamos a buscar
        createTestTrainer("trainer@test.com", "trainer_user", "987654");

        /// Act
        // Buscamos en el LearnerRepository
        boolean exists = learnerRepository.existsByEmail("trainer@test.com");

        /// Assert
        // El repositorio de Learner NO debe encontrar el email del Trainer
        assertThat(exists).isFalse();
    }
}