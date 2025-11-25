package org.idea.fithub.program.infrastructure;

import org.idea.fithub.BaseRepositoryTest;
import org.idea.fithub.learner.domain.Learner;
import org.idea.fithub.program.domain.Program;
import org.idea.fithub.trainer.domain.Trainer;
import org.idea.fithub.user.domain.Gender;
import org.idea.fithub.user.domain.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;

class ProgramRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProgramRepository programRepository;

    private Learner learner1;
    private Learner learner2;
    private Trainer trainer;

    // --- Helpers para crear dependencias ---

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
        learner.setHeight(175.0);
        learner.setWeight(70.0);
        return entityManager.persist(learner);
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
        trainer.setHeight(165.0);
        trainer.setWeight(60.0);
        return entityManager.persist(trainer);
    }

    private void createTestProgram(String name, Learner learner, Trainer trainer) {
        Program program = new Program();
        program.setName(name);
        program.setLearner(learner);
        program.setTrainer(trainer);
        entityManager.persist(program);
    }

    @BeforeEach
    void setUp() {
        /// Arrange global
        learner1 = createTestLearner("learner1@test.com", "learner1", "111111");
        learner2 = createTestLearner("learner2@test.com", "learner2", "222222");
        trainer = createTestTrainer("trainer@test.com", "trainer", "333333");

        createTestProgram("Program A (Learner 1)", learner1, trainer);
        createTestProgram("Program B (Learner 1)", learner1, trainer);

        createTestProgram("Program C (Learner 2)", learner2, trainer);

        entityManager.flush();
    }

    /**
     * Prueba la consulta personalizada: findByLearner
     * Caso: Happy Path (Learner tiene programas)
     * Prueba el FILTRADO y la PAGINACIÓN al mismo tiempo.
     */
    @Test
    void shouldReturnPagedProgramsForCorrectLearner() {
        /// Arrange
        Pageable pageable = PageRequest.of(0, 1);

        /// Act
        Page<Program> programPage = programRepository.findByLearner(learner1, pageable);

        /// Assert
        assertThat(programPage).isNotNull();
        assertThat(programPage.getTotalElements()).isEqualTo(2);
        assertThat(programPage.getTotalPages()).isEqualTo(2);
        assertThat(programPage.getNumber()).isEqualTo(0);
        assertThat(programPage.getContent()).hasSize(1);

        String programName = programPage.getContent().getFirst().getName();
        assertThat(programName).startsWith("Program");
        assertThat(programName).endsWith("(Learner 1)");
        assertThat(programName).doesNotContain("Learner 2");
    }

    /**
     * Prueba la consulta personalizada: findByLearner
     * Caso: Edge Case (Learner existe pero no tiene programas)
     */
    @Test
    void shouldReturnEmptyPageWhenLearnerHasNoPrograms() {
        /// Arrange
        Learner learner3 = createTestLearner("learner3@test.com", "learner3", "444444");
        Pageable pageable = PageRequest.of(0, 10);

        /// Act
        Page<Program> programPage = programRepository.findByLearner(learner3, pageable);

        /// Assert
        assertThat(programPage).isNotNull();
        assertThat(programPage.getTotalElements()).isZero();
        assertThat(programPage.getContent()).isEmpty();
    }

    /**
     * Prueba la consulta personalizada: findByLearner
     * Caso: Edge Case (El Learner ID no existe en la BD)
     */
    @Test
    void shouldReturnEmptyPageWhenLearnerDoesNotExist() {
        /// Arrange
        Learner nonExistentLearner = new Learner();
        nonExistentLearner.setId(9999L);
        // Configurar campos obligatorios para evitar problemas de validación
        nonExistentLearner.setUsername("nonexistent");
        nonExistentLearner.setEmail("nonexistent@test.com");
        nonExistentLearner.setPassword("password");
        nonExistentLearner.setFirstName("Nonexistent");
        nonExistentLearner.setLastName("User");
        nonExistentLearner.setPhoneNumber("999999999");
        nonExistentLearner.setGender(Gender.MALE);
        nonExistentLearner.setHeight(170.0);
        nonExistentLearner.setWeight(65.0);
        nonExistentLearner.setRole(Role.LEARNER);

        Pageable pageable = PageRequest.of(0, 10);

        /// Act
        Page<Program> programPage = programRepository.findByLearner(nonExistentLearner, pageable);

        /// Assert
        assertThat(programPage).isNotNull();
        assertThat(programPage.getTotalElements()).isZero();
        assertThat(programPage.getContent()).isEmpty();
    }
}