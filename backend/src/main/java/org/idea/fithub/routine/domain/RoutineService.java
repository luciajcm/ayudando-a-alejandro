package org.idea.fithub.routine.domain;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.idea.fithub.exercise.domain.Exercise;
import org.idea.fithub.exercise.infrastructure.ExerciseRepository;
import org.idea.fithub.routine.dto.RoutineExerciseDto;
import org.idea.fithub.exceptions.BadRequestException;
import org.idea.fithub.exceptions.DuplicateResourceException;
import org.idea.fithub.exceptions.InvalidOperationException;
import org.idea.fithub.exceptions.ResourceNotFoundException;
import org.idea.fithub.routine.dto.RoutineRequestDto;
import org.idea.fithub.routine.dto.RoutineResponseDto;
import org.idea.fithub.routine.infrastructure.RoutineRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoutineService {
    private final RoutineRepository routineRepository;
    private final ExerciseRepository exerciseRepository;
    private final ModelMapper modelMapper;

    public RoutineResponseDto getRoutine(Long id) {
        var routine = routineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Routine not found with id: " + id));

        return modelMapper.map(routine, RoutineResponseDto.class);
    }

    public Page<RoutineResponseDto> getAllRoutines(PageRequest pageRequest) {
        return routineRepository.findAll(pageRequest)
                .map(routine -> modelMapper.map(routine, RoutineResponseDto.class));
    }

    @Transactional
    public Routine addExercise(Long id, RoutineExerciseDto routineExerciseDto) {
        var routine = routineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Routine not found with id: " + id));

        if (routineExerciseDto.getSets() == null || routineExerciseDto.getSets() <= 0) {
            throw new BadRequestException("Sets must be greater than 0");
        }

        if (routineExerciseDto.getRepetition() == null || routineExerciseDto.getRepetition() <= 0) {
            throw new BadRequestException("Repetition must be greater than 0");
        }

        if (routineExerciseDto.getWeight() != null && routineExerciseDto.getWeight() < 0) {
            throw new BadRequestException("Weight cannot be negative");
        }

        var exerciseName = routineExerciseDto.getExerciseRequestDto().getName();
        var exercise = exerciseRepository.findByName(exerciseName);

        Exercise finalExercise;

        if (exercise.isEmpty()) {
            finalExercise = exerciseRepository.save(
                    modelMapper.map(routineExerciseDto.getExerciseRequestDto(), Exercise.class));
        } else {
            finalExercise = exercise.get();
        }

        boolean exerciseExists = routine.getExercises().stream()
                .anyMatch(re -> re.getExercise().getId().equals(finalExercise.getId()));

        if (exerciseExists) {
            throw new DuplicateResourceException(
                    "Exercise '" + finalExercise.getName() + "' is already in this routine");
        }

        routine.addExercise(finalExercise,
                routineExerciseDto.getSets(),
                routineExerciseDto.getRepetition(),
                routineExerciseDto.getWeight());

        return routineRepository.save(routine);
    }

    @Transactional
    public void deleteExercise(Long routineId, Long exerciseId) {
        var routine = routineRepository.findById(routineId)
                .orElseThrow(() -> new ResourceNotFoundException("Routine not found with id: " + routineId));

        var exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new ResourceNotFoundException("Exercise not found with id: " + exerciseId));

        boolean exerciseExists = routine.getExercises().stream()
                .anyMatch(re -> re.getExercise().getId().equals(exerciseId));

        if (!exerciseExists) {
            throw new ResourceNotFoundException(
                    "Exercise not found in this routine");
        }

        routine.removeExercise(exercise);
        routineRepository.save(routine);
    }

    public List<RoutineExerciseDto> getRoutineExercises(Long id, PageRequest pageRequest) {
        var routine = routineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Routine not found with id: " + id));

        var exercises = routine.getExercises();
        int start = (int) pageRequest.getOffset();
        int end = Math.min((start + pageRequest.getPageSize()), exercises.size());

        if (start > exercises.size()) {
            return List.of();
        }

        return exercises.subList(start, end).stream()
                .map(routineExercise -> modelMapper.map(routineExercise, RoutineExerciseDto.class))
                .toList();
    }

    @Transactional
    public Routine createRoutine(RoutineRequestDto routineRequestDto) {
        if (routineRequestDto.getName() == null || routineRequestDto.getName().isBlank()) {
            throw new BadRequestException("Routine name is required");
        }

        if (routineRequestDto.getDay() == null) {
            throw new BadRequestException("Routine day is required");
        }

        if (routineRepository.existsByNameAndDay(routineRequestDto.getName(), routineRequestDto.getDay())) {
            throw new DuplicateResourceException(
                    "A routine with name '" + routineRequestDto.getName() +
                            "' for " + routineRequestDto.getDay() + " already exists");
        }

        return routineRepository.save(
                modelMapper.map(routineRequestDto, Routine.class));
    }

    @Transactional
    public RoutineResponseDto updateRoutine(Long id, RoutineRequestDto routineRequestDto) {
        var routine = routineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Routine not found with id: " + id));

        if (routineRequestDto.getName() == null || routineRequestDto.getName().isBlank()) {
            throw new BadRequestException("Routine name is required");
        }

        if (routineRequestDto.getDay() == null) {
            throw new BadRequestException("Routine day is required");
        }

        var existingRoutine = routineRepository.findByNameAndDay(
                routineRequestDto.getName(),
                routineRequestDto.getDay());

        if (existingRoutine.isPresent() && !existingRoutine.get().getId().equals(id)) {
            throw new DuplicateResourceException(
                    "A routine with name '" + routineRequestDto.getName() +
                            "' for " + routineRequestDto.getDay() + " already exists");
        }

        modelMapper.map(routineRequestDto, routine);

        return modelMapper.map(
                routineRepository.save(routine), RoutineResponseDto.class);
    }

    @Transactional
    public void deleteRoutine(Long id) {
        var routine = routineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Routine not found with id: " + id));

        if (!routine.getPrograms().isEmpty()) {
            throw new InvalidOperationException(
                    "Cannot delete routine. It is assigned to " +
                            routine.getPrograms().size() + " program(s)");
        }

        routineRepository.delete(routine);
    }
}