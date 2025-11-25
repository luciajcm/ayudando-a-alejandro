package org.idea.fithub.trainer.infrastructure;

import org.idea.fithub.trainer.domain.Trainer;
import org.idea.fithub.user.infrastructure.BaseUserRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainerRepository extends BaseUserRepository<Trainer> {
    boolean existsByEmail(String email);
}