package org.idea.fithub.trainingCheck.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.idea.fithub.learner.domain.Learner;

import java.time.LocalDate;

@Entity
@Table(name = "training_check")
@NoArgsConstructor
public class TrainingCheck {
    @Id
    @Getter
    @Setter
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Getter
    @Setter
    @Column(nullable = false)
    private LocalDate date;

    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "learner_id", nullable = false)
    private Learner learner;

    public TrainingCheck(LocalDate date, Learner learner) {
        this.date = date;
        this.learner = learner;
    }
}