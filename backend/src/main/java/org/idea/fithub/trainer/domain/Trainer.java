package org.idea.fithub.trainer.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.idea.fithub.program.domain.Program;
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
@SuperBuilder(toBuilder = true)
@Table(name = "trainers")
public class Trainer extends User {

    @Column(name = "experience_start_date")
    private LocalDate experienceStartDate;

    @Column(name = "experience_end_date")
    private LocalDate experienceEndDate;

    @Column(name = "experience_time")
    private String experienceTime;

    @OneToMany(mappedBy = "trainer")
    private List<Program> programs;

    public String experienceTime() {
        if (experienceStartDate == null)
            return null;

        var period = Period.between(
                experienceStartDate,
                (experienceEndDate != null) ? experienceEndDate : LocalDate.now());

        return String.format("%d years, %d months", period.getYears(), period.getMonths());
    }
}
