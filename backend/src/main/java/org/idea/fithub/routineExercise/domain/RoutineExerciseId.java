package org.idea.fithub.routineExercise.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoutineExerciseId implements Serializable {
    @Column
    private Long routineId;

    @Column
    private Long exerciseId;

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (obj == null || getClass() != obj.getClass())
            return false;

        RoutineExerciseId that = (RoutineExerciseId) obj;

        return Objects.equals(routineId, that.routineId) &&
                Objects.equals(exerciseId, that.exerciseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(routineId, exerciseId);
    }
}