package org.idea.fithub.routine.infrastructure;

import org.idea.fithub.BaseRepositoryTest;
import org.idea.fithub.routine.domain.Day;
import org.idea.fithub.routine.domain.Routine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class RoutineRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RoutineRepository routineRepository;

    private Routine existingRoutine;

    @BeforeEach
    void setUp() {
        /// Arrange global para casos de borde
        existingRoutine = new Routine();
        existingRoutine.setName("Upper Body");
        existingRoutine.setDay(Day.MONDAY);
        entityManager.persistAndFlush(existingRoutine);
    }

    //--- Pruebas para findByNameAndDay ---

    /**
     * Prueba la consulta personalizada: findByNameAndDay
     * Caso: Happy Path (Nombre y Día coinciden)
     */
    @Test
    void shouldFindRoutineByNameAndDayWhenExists() {
        /// Arrange
        // Ya existe "Upper Body" - MONDAY desde el setUp

        /// Act
        Optional<Routine> found = routineRepository.findByNameAndDay("Upper Body", Day.MONDAY);

        /// Assert
        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(existingRoutine.getId());
        assertThat(found.get().getName()).isEqualTo("Upper Body");
    }

    /**
     * Prueba la consulta personalizada: findByNameAndDay
     * Caso: Edge Case (Nombre coincide, Día NO coincide)
     * Esto prueba que la lógica 'AND' de la consulta funciona.
     */
    @Test
    void shouldReturnEmptyOptionalWhenNameMatchesButDayDoesNot() {
        /// Arrange
        // Ya existe "Upper Body" - MONDAY

        /// Act
        Optional<Routine> found = routineRepository.findByNameAndDay("Upper Body", Day.TUESDAY);

        /// Assert
        assertThat(found).isNotPresent();
    }

    /**
     * Prueba la consulta personalizada: findByNameAndDay
     * Caso: Edge Case (Día coincide, Nombre NO coincide)
     * Esto también prueba la lógica 'AND'.
     */
    @Test
    void shouldReturnEmptyOptionalWhenDayMatchesButNameDoesNot() {
        /// Arrange
        // Ya existe "Upper Body" - MONDAY

        /// Act
        Optional<Routine> found = routineRepository.findByNameAndDay("Lower Body", Day.MONDAY);

        /// Assert
        assertThat(found).isNotPresent();
    }

    /**
     * Prueba la consulta personalizada: findByNameAndDay
     * Caso: Negativo (Nada coincide)
     */
    @Test
    void shouldReturnEmptyOptionalWhenRoutineDoesNotExist() {
        /// Arrange
        // La base de datos solo tiene "Upper Body" - MONDAY

        /// Act
        Optional<Routine> found = routineRepository.findByNameAndDay("Cardio Day", Day.FRIDAY);

        /// Assert
        assertThat(found).isNotPresent();
    }

    //--- Pruebas para existsByNameAndDay ---

    /**
     * Prueba la consulta personalizada: existsByNameAndDay
     * Caso: Happy Path (Nombre y Día coinciden)
     */
    @Test
    void shouldReturnTrueWhenRoutineExistsByNameAndDay() {
        /// Arrange
        // Ya existe "Upper Body" - MONDAY

        /// Act
        boolean exists = routineRepository.existsByNameAndDay("Upper Body", Day.MONDAY);

        /// Assert
        assertThat(exists).isTrue();
    }

    /**
     * Prueba la consulta personalizada: existsByNameAndDay
     * Caso: Edge Case (Nombre coincide, Día NO coincide)
     */
    @Test
    void shouldReturnFalseWhenNameMatchesButDayDoesNot() {
        /// Arrange
        // Ya existe "Upper Body" - MONDAY

        /// Act
        boolean exists = routineRepository.existsByNameAndDay("Upper Body", Day.WEDNESDAY);

        /// Assert
        assertThat(exists).isFalse();
    }

    /**
     * Prueba la consulta personalizada: existsByNameAndDay
     * Caso: Negativo (Nada coincide)
     */
    @Test
    void shouldReturnFalseWhenRoutineDoesNotExist() {
        /// Arrange
        // La base de datos solo tiene "Upper Body" - MONDAY

        /// Act
        boolean exists = routineRepository.existsByNameAndDay("Leg Day", Day.TUESDAY);

        /// Assert
        assertThat(exists).isFalse();
    }
}