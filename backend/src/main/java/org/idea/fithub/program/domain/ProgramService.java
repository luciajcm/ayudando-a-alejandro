package org.idea.fithub.program.domain;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.idea.fithub.exceptions.*;
import org.idea.fithub.learner.infrastructure.LearnerRepository;
import org.idea.fithub.program.dto.ProgramRequestDto;
import org.idea.fithub.program.dto.ProgramResponseDto;
import org.idea.fithub.program.infrastructure.ProgramRepository;
import org.idea.fithub.routine.infrastructure.RoutineRepository;
import org.idea.fithub.trainer.infrastructure.TrainerRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ProgramService {
    private final ProgramRepository programRepository;
    private final LearnerRepository learnerRepository;
    private final TrainerRepository trainerRepository;
    private final RoutineRepository routineRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public ProgramResponseDto createProgram(ProgramRequestDto programRequestDto) {
        if (programRequestDto.getLearnerId() == null)
            throw new BadRequestException("Learner id is required");

        if (programRequestDto.getTrainerId() == null)
            throw new BadRequestException("Trainer id is required");

        if (programRequestDto.getStartDate() != null && programRequestDto.getEndDate() != null)
            if (programRequestDto.getEndDate().isBefore(programRequestDto.getStartDate())) {
                throw new BadRequestException("End date cannot be before start date");
            }

        if (programRequestDto.getStartDate() != null &&
                programRequestDto.getStartDate().isBefore(LocalDate.now())) {
            throw new BadRequestException("Start date cannot be before today");
        }

        var learner = learnerRepository
                .findById(programRequestDto.getLearnerId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Learner not found with id: " + programRequestDto.getLearnerId()));

        if (programRequestDto.getName() != null && !programRequestDto.getName().isBlank()) {
            boolean exists = learner.getPrograms().stream()
                    .anyMatch(program -> program.getName() != null &&
                            program.getName().equalsIgnoreCase(programRequestDto.getName()));
            if (exists) {
                throw new DuplicateResourceException(
                        "Program with name '" + programRequestDto.getName() +
                                "' already exists for this learner.");
            }
        }

        var program = modelMapper.map(programRequestDto, Program.class);
        program.setLearner(learner);
        program.setCreatedAt(LocalDate.now());

        var trainer = trainerRepository
                .findById(programRequestDto.getTrainerId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Trainer not found with id: " + programRequestDto.getTrainerId()));
        program.setTrainer(trainer);

        return modelMapper.map(
                programRepository.save(program), ProgramResponseDto.class);
    }

    public ProgramResponseDto assignRoutineToProgram(Long id, Long routineId) {
        var program = programRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Program not found with id: " + id));

        var routine = routineRepository.findById(routineId)
                .orElseThrow(() -> new ResourceNotFoundException("Routine not found with id: " + routineId));

        if (program.getProgramRoutines().contains(routine))
            throw new DuplicateResourceException("Routine already added to this program");

        if (program.getEndDate() != null && program.getEndDate().isBefore(LocalDate.now()))
            throw new InvalidOperationException(
                    "Cannot assign routine to a finished program (ended on " + program.getEndDate() + ")");

        program.getProgramRoutines().add(routine);

        return modelMapper.map(
                programRepository.save(program), ProgramResponseDto.class);
    }

    public Page<ProgramResponseDto> getProgramsByLearner(Long learnerId, PageRequest pageRequest) {
        var learner = learnerRepository.findById(learnerId)
                .orElseThrow(() -> new ResourceNotFoundException("Learner not found with id: " + learnerId));

        return programRepository.findByLearner(learner, pageRequest)
                .map(programResponseDto -> modelMapper
                        .map(programResponseDto, ProgramResponseDto.class));
    }

    @Transactional
    public void deleteProgram(Long id) {
        var program = programRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Program not found with id: " + id));

        if (program.getStartDate() != null && program.getEndDate() != null) {
            LocalDate now = LocalDate.now();
            boolean isActive = !now.isBefore(program.getStartDate()) && !now.isAfter(program.getEndDate());

            if (isActive) {
                throw new InvalidOperationException(
                        "Cannot delete an active program. Program is active from " +
                                program.getStartDate() + " to " + program.getEndDate());
            }
        }

        programRepository.delete(program);
    }

    @Transactional
    public ProgramResponseDto updateProgram(Long id, ProgramRequestDto programRequestDto) {
        var program = programRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Program not found with id: " + id));

        if (programRequestDto.getStartDate() != null && programRequestDto.getEndDate() != null) {
            if (programRequestDto.getEndDate().isBefore(programRequestDto.getStartDate())) {
                throw new BadRequestException("End date cannot be before start date");
            }
        }

        if (programRequestDto.getStartDate() != null &&
                programRequestDto.getStartDate().isBefore(LocalDate.now())) {
            throw new BadRequestException("Start date cannot be before today");
        }

        if (programRequestDto.getTrainerId() != null) {
            var trainer = trainerRepository.findById(programRequestDto.getTrainerId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Trainer not found with id: " + programRequestDto.getTrainerId()));
            program.setTrainer(trainer);
        }

        modelMapper.map(programRequestDto, program);

        var updatedProgram = programRepository.save(program);
        return modelMapper.map(updatedProgram, ProgramResponseDto.class);
    }

}