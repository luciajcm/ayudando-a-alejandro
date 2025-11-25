package org.idea.fithub.trainingCheck.domain;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.idea.fithub.exceptions.BadRequestException;
import org.idea.fithub.exceptions.DuplicateResourceException;
import org.idea.fithub.exceptions.ResourceNotFoundException;
import org.idea.fithub.learner.infrastructure.LearnerRepository;
import org.idea.fithub.trainingCheck.dto.TrainingCheckResponseDto;
import org.idea.fithub.trainingCheck.infrastructure.TrainingCheckRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class TrainingCheckService {
    private final TrainingCheckRepository trainingCheckRepository;
    private final LearnerRepository learnerRepository;

    @Transactional
    public void markCheck(Long learnerId, LocalDate date) {
        var learner = learnerRepository.findById(learnerId)
                .orElseThrow(() -> new ResourceNotFoundException("Learner not found with id: " + learnerId));

        if (date == null) {
            throw new BadRequestException("Date cannot be null");
        }

        if (date.isAfter(LocalDate.now())) {
            throw new BadRequestException("Cannot mark attendance for a future date: " + date);
        }

        if (date.isBefore(LocalDate.now().minusYears(1))) {
            throw new BadRequestException("Cannot mark attendance for dates older than 1 year");
        }

        if (trainingCheckRepository.existsByLearnerAndDate(learner, date))
            throw new DuplicateResourceException("Learner has already marked attendance for this day.");

        trainingCheckRepository.save(
                new TrainingCheck(date, learner));
    }

    @Transactional
    public void unmarkCheck(Long learnerId, LocalDate date) {
        var learner = learnerRepository.findById(learnerId)
                .orElseThrow(() -> new ResourceNotFoundException("Learner not found with id: " + learnerId));

        if (date == null) {
            throw new BadRequestException("Date cannot be null");
        }

        var trainingCheck = trainingCheckRepository.findByLearnerAndDate(learner, date)
                .orElseThrow(() -> new ResourceNotFoundException("There is no register for that day"));

        trainingCheckRepository.delete(trainingCheck);
    }

    public TrainingCheckResponseDto getTrainingCheck(Long learnerId) {
        var learner = learnerRepository.findById(learnerId)
                .orElseThrow(() -> new ResourceNotFoundException("Learner not found with id: " + learnerId));

        var learnerChecks = trainingCheckRepository.findByLearner(learner);
        var checkDates = learnerChecks.stream()
                .map(TrainingCheck::getDate)
                .sorted()
                .toList();

        return new TrainingCheckResponseDto(
                learner.getId(),
                checkDates.size(),
                learner.calculateDuration(checkDates),
                checkDates);
    }
}