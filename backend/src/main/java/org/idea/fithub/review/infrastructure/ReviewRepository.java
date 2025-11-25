package org.idea.fithub.review.infrastructure;

import org.idea.fithub.review.domain.Review;
import org.idea.fithub.review.dto.TrainerReviewResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<TrainerReviewResponseDto> findByTargetId(Long targetId, Pageable pageable);

    boolean existsByProgramId(Long programId);
}
