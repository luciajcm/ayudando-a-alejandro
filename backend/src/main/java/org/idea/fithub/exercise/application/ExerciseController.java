package org.idea.fithub.exercise.application;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.idea.fithub.exercise.domain.ExerciseService;
import org.idea.fithub.exercise.domain.Muscle;
import org.idea.fithub.exercise.dto.ExerciseRequestDto;
import org.idea.fithub.exercise.dto.ExerciseResponseDto;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/exercises")
public class ExerciseController {
    private final ExerciseService exerciseService;
    private final ModelMapper modelMapper;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LEARNER','TRAINER')")
    public ResponseEntity<Page<ExerciseResponseDto>> getAllExercises(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return ResponseEntity.ok(
                exerciseService.getAllExercises(PageRequest.of(page, size)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LEARNER','TRAINER')")
    public ResponseEntity<ExerciseResponseDto> getExercise(@PathVariable Long id) {
        return ResponseEntity.ok(
                exerciseService.getExercise(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ExerciseResponseDto> createExercise(@Valid @RequestBody
                                                   ExerciseRequestDto exerciseRequestDto) {
        var exercise = exerciseService.createExercise(exerciseRequestDto);
        var location = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/{id}")
                .buildAndExpand(exercise.getId())
                .toUri();

        var responseDto = modelMapper.map(exercise, ExerciseResponseDto.class);

        return ResponseEntity.created(location).body(responseDto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ExerciseResponseDto> updateExercise(@PathVariable Long id,
                                                              @Valid @RequestBody
                                                              ExerciseRequestDto exerciseRequestDto) {
        return ResponseEntity.ok(
                exerciseService.updateExercise(id, exerciseRequestDto));
    }

    @PatchMapping("/{id}/muscle")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ExerciseResponseDto> updateExercise(@PathVariable Long id,
                                                              @RequestParam Muscle muscle) {
        return ResponseEntity.ok(
                exerciseService.updateExercise(id, muscle));
    }

    @PatchMapping("/{id}/asset")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ExerciseResponseDto> updateExercise(@PathVariable Long id,
                                                              @RequestParam String asset) {
        return ResponseEntity.ok(
                exerciseService.updateExercise(id, asset));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteExercise(@PathVariable Long id) {
        exerciseService.deleteExercise(id);
        return ResponseEntity.noContent().build();
    }
}