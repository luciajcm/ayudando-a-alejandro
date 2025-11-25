package org.idea.fithub.exercise.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.idea.fithub.routineExercise.domain.RoutineExercise;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Table(name = "exercises")
public class Exercise {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    private String asset;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Muscle muscle;

    @OneToMany(mappedBy = "exercise",
            orphanRemoval = true)
    private List<RoutineExercise> routines = new ArrayList<>();
}