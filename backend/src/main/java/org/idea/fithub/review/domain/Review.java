package org.idea.fithub.review.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Check;
import org.idea.fithub.learner.domain.Learner;
import org.idea.fithub.program.domain.Program;
import org.idea.fithub.trainer.domain.Trainer;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Check(constraints = "rating BETWEEN 0 AND 5")
@Table(name = "reviews")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private Integer rating;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private Learner author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_id", nullable = false)
    private Trainer target;

    @OneToOne
    @JoinColumn(name = "program_id", nullable = false, unique = true)
    private Program program;

    @Column(nullable = false)
    private String comment;
}
