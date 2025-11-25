package org.idea.fithub.routine.application;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.idea.fithub.routine.domain.RoutineService;
import org.idea.fithub.routine.dto.RoutineExerciseDto;
import org.idea.fithub.routine.dto.RoutineRequestDto;
import org.idea.fithub.routine.dto.RoutineResponseDto;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/routines")
@RequiredArgsConstructor
public class RoutineController {
    private final RoutineService routineService;
    private final ModelMapper modelMapper;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','TRAINER','LEARNER')")
    public ResponseEntity<RoutineResponseDto> createRoutine(@Valid @RequestBody
                                                 RoutineRequestDto routineRequestDto) {
        var routine = routineService.createRoutine(routineRequestDto);
        var location = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/{id}")
                .buildAndExpand(routine.getId())
                .toUri();

        var responseDto = modelMapper.map(routine, RoutineResponseDto.class);

        return ResponseEntity.created(location).body(responseDto);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','TRAINER','LEARNER')")
    public ResponseEntity<RoutineResponseDto> getRoutine(@PathVariable Long id) {
        return ResponseEntity.ok(
                routineService.getRoutine(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','TRAINER','LEARNER')")
    public ResponseEntity<Page<RoutineResponseDto>> getAllRoutines(@RequestParam(defaultValue = "0") Integer page,
                                                                   @RequestParam(defaultValue = "10") Integer size) {
        return ResponseEntity.ok(
                routineService.getAllRoutines(PageRequest.of(page, size)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','TRAINER','LEARNER')")
    public ResponseEntity<RoutineResponseDto> updateRoutine(@PathVariable Long id,
                                                            @Valid @RequestBody
                                                            RoutineRequestDto routineRequestDto) {
        return ResponseEntity.ok(
                routineService.updateRoutine(id, routineRequestDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','TRAINER','LEARNER')")
    public ResponseEntity<Void> deleteRoutine(@PathVariable Long id) {
        routineService.deleteRoutine(id);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/exercise")
    @PreAuthorize("hasAnyRole('ADMIN','TRAINER','LEARNER')")
    public ResponseEntity<RoutineResponseDto> addExercise(@PathVariable Long id,
                                               @RequestBody RoutineExerciseDto routineExerciseDto) {
        var routineExercise = routineService.addExercise(id, routineExerciseDto);
        var location = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/api/routines/{id}")
                .buildAndExpand(routineExercise.getId())
                .toUri();

        var responseDto = modelMapper.map(routineExercise, RoutineResponseDto.class);

        return ResponseEntity.ok().location(location).body(responseDto);
    }

    @DeleteMapping("/{routineId}/exercise/{exerciseId}")
    @PreAuthorize("hasAnyRole('ADMIN','TRAINER','LEARNER')")
    public ResponseEntity<Void> deleteExercise(@PathVariable Long routineId,
                                               @PathVariable Long exerciseId) {
        routineService.deleteExercise(routineId, exerciseId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/exercise")
    @PreAuthorize("hasAnyRole('ADMIN','TRAINER','LEARNER')")
    public ResponseEntity<List<RoutineExerciseDto>> getRoutineExercises(@PathVariable Long id,
                                                                        @RequestParam(defaultValue = "0") Integer page,
                                                                        @RequestParam(defaultValue = "10") Integer size) {
        return ResponseEntity.ok(
                routineService.getRoutineExercises(id, PageRequest.of(page, size)));
    }
}