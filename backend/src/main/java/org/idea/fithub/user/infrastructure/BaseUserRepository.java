package org.idea.fithub.user.infrastructure;

import jakarta.validation.constraints.NotBlank;
import org.idea.fithub.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BaseUserRepository<T extends User> extends JpaRepository<T, Long>{
    Optional<T> findByEmail(String email);

    boolean existsByUsername(@NotBlank String username);
    boolean existsByEmail(String email);
}