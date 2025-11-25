package org.idea.fithub.routine.infrastructure;

import org.idea.fithub.routine.domain.Day;
import org.idea.fithub.routine.domain.Routine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoutineRepository extends JpaRepository<Routine, Long> {
    boolean existsByNameAndDay(String name, Day day);
    Optional<Routine> findByNameAndDay(String name, Day day);
}
