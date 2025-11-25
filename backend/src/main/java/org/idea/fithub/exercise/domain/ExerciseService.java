package org.idea.fithub.exercise.domain;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.idea.fithub.exceptions.ConflictException;
import org.idea.fithub.exceptions.DuplicateResourceException;
import org.idea.fithub.exceptions.ResourceNotFoundException;
import org.idea.fithub.exceptions.BadRequestException;
import org.idea.fithub.exercise.dto.ExerciseRequestDto;
import org.idea.fithub.exercise.dto.ExerciseResponseDto;
import org.idea.fithub.exercise.infrastructure.ExerciseRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExerciseService {
    private final ExerciseRepository exerciseRepository;
    private final ModelMapper modelMapper;

    public Page<ExerciseResponseDto> getAllExercises(PageRequest pageRequest) {
        return exerciseRepository.findAll(pageRequest)
                .map(exercise -> modelMapper.map(exercise, ExerciseResponseDto.class));
    }

    public ExerciseResponseDto getExercise(Long id) {
        var exercise = exerciseRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("Exercise not found with id: " + id));

        return modelMapper.map(exercise, ExerciseResponseDto.class);
    }

    @Transactional
    public Exercise createExercise(ExerciseRequestDto exerciseRequestDto) {
        exerciseRepository.findByName(exerciseRequestDto.getName())
                .ifPresent(e -> {
                    throw new DuplicateResourceException(
                            "Exercise already exists with name: " + exerciseRequestDto.getName());
                });

        var exercise = modelMapper.map(exerciseRequestDto, Exercise.class);

        return exerciseRepository.save(exercise);
    }

    @Transactional
    public void deleteExercise(Long id) {
        var exercise = exerciseRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("Exercise not found with id: " + id));

        if (!exercise.getRoutines().isEmpty()) {
            throw new ConflictException(
                    "Cannot delete exercise. It is being used in " +
                            exercise.getRoutines().size() + " routines.");
        }

        exerciseRepository.delete(exercise);
    }

    @Transactional
    public ExerciseResponseDto updateExercise(Long id, Muscle muscle) {
        if (muscle == null) {
            throw new BadRequestException("Exercise muscle is required.");
        }

        Exercise exercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exercise not found with id: " + id));

        exercise.setMuscle(muscle);
        Exercise savedExercise = exerciseRepository.save(exercise);

        return modelMapper.map(savedExercise, ExerciseResponseDto.class);
    }

    @Transactional
    public ExerciseResponseDto updateExercise(Long id, String asset) {
        if (asset == null || asset.trim().isEmpty()) {
            throw new BadRequestException("Exercise asset is required.");
        }

        Exercise exercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exercise not found with id: " + id));

        exercise.setAsset(asset);
        Exercise savedExercise = exerciseRepository.save(exercise);

        return modelMapper.map(savedExercise, ExerciseResponseDto.class);
    }

    @Transactional
    public ExerciseResponseDto updateExercise(Long id, ExerciseRequestDto exerciseRequestDto) {
        var exercise = exerciseRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("Exercise not found with id: " + id));

        String newName = exerciseRequestDto.getName();
        if (newName != null && !newName.equals(exercise.getName())) {
            exerciseRepository.findByName(newName).ifPresent(e -> {
                throw new DuplicateResourceException("Exercise already exists with name: " + newName);
            });
        }

        modelMapper.map(exerciseRequestDto, exercise);
        Exercise savedExercise = exerciseRepository.save(exercise);

        return modelMapper.map(savedExercise, ExerciseResponseDto.class);
    }
}
