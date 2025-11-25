package org.idea.fithub.routine.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.idea.fithub.exercise.domain.Exercise;
import org.idea.fithub.program.domain.Program;
import org.idea.fithub.routineExercise.domain.RoutineExercise;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Table(name = "routines")
public class Routine {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Day day;

    @Column(nullable = false, length = 100)
    private String name;

    @ManyToMany(mappedBy = "programRoutines")
    private List<Program> programs;

    @OneToMany(mappedBy = "routine",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER)
    private List<RoutineExercise> exercises = new ArrayList<>();

    public void addExercise(Exercise exercise, Integer sets, Integer repetition, Double weight) {
        var routineExercise = new RoutineExercise(this, exercise, sets, repetition, weight);
        exercises.add(routineExercise);
        exercise.getRoutines().add(routineExercise);
    }

    public void removeExercise(Exercise exercise) {
        for (var routineExercise : exercises)
            if (routineExercise.getRoutine().equals(this) &&
                    Objects.equals(routineExercise.getExercise().getId(), exercise.getId())) {
                exercises.remove(routineExercise);

                routineExercise.getExercise().getRoutines().remove(routineExercise);
                routineExercise.setRoutine(null);
                routineExercise.setExercise(null);

                break;
            }
    }
}
