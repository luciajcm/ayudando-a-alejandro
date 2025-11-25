package org.idea.fithub.learner.domain;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.idea.fithub.exceptions.BadRequestException;
import org.idea.fithub.exceptions.ConflictException;
import org.idea.fithub.exceptions.DuplicateResourceException;
import org.idea.fithub.exceptions.ResourceNotFoundException;
import org.idea.fithub.learner.dto.LearnerRequestDto;
import org.idea.fithub.learner.dto.LearnerResponseDto;
import org.idea.fithub.learner.dto.LearnerStatsDto;
import org.idea.fithub.learner.infrastructure.LearnerRepository;
import org.idea.fithub.trainingCheck.infrastructure.TrainingCheckRepository;
import org.idea.fithub.trainer.domain.GoalType;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LearnerService {
    private final LearnerRepository learnerRepository;
    private final TrainingCheckRepository trainingCheckRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    public Page<LearnerResponseDto> getAllLearners(PageRequest pageRequest) {
        return learnerRepository.findAll(pageRequest)
                .map(learnerResponseDto -> modelMapper
                        .map(learnerResponseDto, LearnerResponseDto.class));
    }

    public LearnerResponseDto getLearner(Long id) {
        var learner = learnerRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("Learner not found with id: " + id));

        return modelMapper.map(
                learner, LearnerResponseDto.class);
    }

    @Transactional
    public Learner createLearner(LearnerRequestDto learnerRequestDto) {
        if (learnerRepository.existsByEmail(learnerRequestDto.getEmail()))
            throw new DuplicateResourceException("Email already in use: " + learnerRequestDto.getEmail());

        var learner = modelMapper.map(learnerRequestDto, Learner.class);
        learner.setPassword(passwordEncoder.encode(learnerRequestDto.getPassword()));
        return learnerRepository.save(learner);
    }

    @Transactional
    public void deleteLearner(Long id) {
        var learner = learnerRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("Learner not found with id: " + id));

        if (!learner.getPrograms().isEmpty())
            throw new ConflictException(
                    "Cannot delete learner. Has " +
                            learner.getPrograms().size() + " active program(s).");

        if (!learner.getTrainingChecks().isEmpty())
            throw new ConflictException(
                    "Cannot delete learner. Has " +
                            learner.getTrainingChecks().size() + " training check(s).");

        learnerRepository.delete(learner);
    }

    @Transactional
    public LearnerResponseDto updateLearner(Long id, LearnerRequestDto learnerRequestDto) {
        Learner learner = learnerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Learner not found with id: " + id));

        String newEmail = learnerRequestDto.getEmail();
        if (newEmail != null && !newEmail.equals(learner.getEmail())) {
            if (learnerRepository.existsByEmail(newEmail)) {
                throw new DuplicateResourceException("Email already in use: " + newEmail);
            }
        }

        modelMapper.map(learnerRequestDto, learner);
        Learner savedLearner = learnerRepository.save(learner);

        return modelMapper.map(savedLearner, LearnerResponseDto.class);
    }

    @Transactional
    public LearnerResponseDto updateLearnerGoal(Long id, GoalType goalType) {
        var learner = learnerRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("Learner not found with id: " + id));

        if (goalType == null)
            throw new BadRequestException("Goal type is required.");

        learner.setGoalType(goalType);

        return modelMapper.map(
                learnerRepository.save(learner), LearnerResponseDto.class);
    }

    @Transactional
    public LearnerResponseDto updateLearnerStats(Long id, LearnerStatsDto learnerStatsDto) {
        var learner = learnerRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("Learner not found with id: " + id));

        if (learnerStatsDto.getWeight() == null || learnerStatsDto.getWeight() <= 0)
            throw new BadRequestException("Weight must be greater than 0");

        if (learnerStatsDto.getHeight() == null || learnerStatsDto.getHeight() <= 0)
            throw new BadRequestException("Height must be greater than 0.");

        if (learnerStatsDto.getGoalType() == null)
            throw new BadRequestException("Goal type is required.");

        learner.setWeight(learnerStatsDto.getWeight());
        learner.setHeight(learnerStatsDto.getHeight());
        learner.setGoalType(learnerStatsDto.getGoalType());

        return modelMapper.map(
                learnerRepository.save(learner), LearnerResponseDto.class);
    }
}