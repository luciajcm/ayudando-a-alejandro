package org.idea.fithub.trainingCheck.infrastructure;

import org.idea.fithub.learner.domain.Learner;
import org.idea.fithub.trainingCheck.domain.TrainingCheck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TrainingCheckRepository extends JpaRepository<TrainingCheck, Long> {
    boolean existsByLearnerAndDate(Learner learner, LocalDate date);

    Optional<TrainingCheck> findByLearnerAndDate(Learner learner, LocalDate date);

    Integer countByLearner(Learner learner);

    List<TrainingCheck> findByLearner(Learner learner);
}