package org.idea.fithub.routineExercise.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.idea.fithub.exercise.domain.Exercise;
import org.idea.fithub.routine.domain.Routine;

@Entity
@NoArgsConstructor
@Getter
@Setter
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Table(name = "routine_exercises")
public class RoutineExercise {
    @EmbeddedId
    private RoutineExerciseId routineExerciseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    @MapsId("routineId")
    private Routine routine;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    @MapsId("exerciseId")
    private Exercise exercise;

    @Column(nullable = false)
    private Integer sets;

    @Column(nullable = false)
    private Integer repetition;

    private Double weight;

    public RoutineExercise(Routine routine, Exercise exercise,
                           Integer sets, Integer repetition, Double weight) {
        if (routine == null) {
            throw new IllegalArgumentException("Routine cannot be null");
        }
        if (exercise == null) {
            throw new IllegalArgumentException("Exercise cannot be null");
        }
        if (sets == null || sets <= 0) {
            throw new IllegalArgumentException("Sets must be greater than 0");
        }
        if (repetition == null || repetition <= 0) {
            throw new IllegalArgumentException("Repetition must be greater than 0");
        }
        if (weight != null && weight < 0) {
            throw new IllegalArgumentException("Weight cannot be negative");
        }

        this.routine = routine;
        this.exercise = exercise;
        this.sets = sets;
        this.repetition = repetition;
        this.weight = weight;
        this.routineExerciseId = new RoutineExerciseId(routine.getId(), exercise.getId());
    }
}