package org.idea.fithub.learner.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder; // Importa SuperBuilder
import org.idea.fithub.program.domain.Program;
import org.idea.fithub.trainingCheck.domain.TrainingCheck;
import org.idea.fithub.trainer.domain.GoalType;
import org.idea.fithub.user.domain.User;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@PrimaryKeyJoinColumn(name = "id")
@Table(name = "learners")
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class Learner extends User {

    @Enumerated(EnumType.STRING)
    @Column(name = "goal_type")
    private GoalType goalType;

    private String duration;

    @OneToMany(mappedBy = "learner")
    private List<TrainingCheck> trainingChecks;

    @OneToMany(mappedBy = "learner")
    private List<Program> programs;

    public String calculateDuration(List<LocalDate> checkDates) {
        if (checkDates == null || checkDates.isEmpty()) return "Without registers";

        var period = Period.between(
                checkDates.getFirst(), checkDates.getLast().plusDays(1));

        return String.format("%d months and %d days", period.getMonths(), period.getDays());
    }
}
