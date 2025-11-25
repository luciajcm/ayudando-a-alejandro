package org.idea.fithub.program.infrastructure;

import org.idea.fithub.learner.domain.Learner;
import org.idea.fithub.program.domain.Program;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProgramRepository extends JpaRepository<Program, Long> {
    Page<Program> findByLearner(Learner learner, Pageable pageable);

    Optional<Program> findByName(String name);
}
