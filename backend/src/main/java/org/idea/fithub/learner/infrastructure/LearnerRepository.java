package org.idea.fithub.learner.infrastructure;

import org.idea.fithub.learner.domain.Learner;
import org.idea.fithub.program.domain.Program;
import org.idea.fithub.user.infrastructure.BaseUserRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LearnerRepository extends BaseUserRepository<Learner> {
    boolean existsByEmail(String email);

    Optional<Learner> findById(Long id);
}
