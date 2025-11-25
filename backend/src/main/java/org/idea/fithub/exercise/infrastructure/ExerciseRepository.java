package org.idea.fithub.exercise.infrastructure;

import org.idea.fithub.exercise.domain.Exercise;
import org.idea.fithub.exercise.domain.Muscle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    Optional<Exercise> findByName(String name);

    boolean existsByMuscle(Muscle muscle);

    boolean existsByAsset(String asset);

    boolean existsByName(String name);
}
