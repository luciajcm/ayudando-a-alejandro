package org.idea.fithub.trainer.domain;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.idea.fithub.exceptions.*;
import org.idea.fithub.trainer.dto.TrainerExperienceDto;
import org.idea.fithub.trainer.dto.TrainerRequestDto;
import org.idea.fithub.trainer.dto.TrainerResponseDto;
import org.idea.fithub.trainer.infrastructure.TrainerRepository;
import org.idea.fithub.user.domain.UserStatus;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@RequiredArgsConstructor
public class TrainerService {
    private final TrainerRepository trainerRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;


    public Page<TrainerResponseDto> getAllTrainers(PageRequest pageRequest) {
        return trainerRepository.findAll(pageRequest)
                .map(trainerResponseDto -> modelMapper
                        .map(trainerResponseDto, TrainerResponseDto.class));
    }

    public TrainerResponseDto getTrainer(Long id) {
        var trainer = trainerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer not found with id: " + id));

        return modelMapper.map(
                trainer, TrainerResponseDto.class);
    }

    @Transactional
    public Trainer createTrainer(TrainerRequestDto trainerRequestDto) {
        if (trainerRepository.existsByEmail(trainerRequestDto.getEmail()))
            throw new DuplicateResourceException("Email already in use: " + trainerRequestDto.getEmail());

        if (trainerRequestDto.getExperienceEndDate() != null &&
                trainerRequestDto.getExperienceEndDate().isBefore(trainerRequestDto.getExperienceStartDate()))
            throw new BadRequestException("End date cannot be before start date");

        var trainner = modelMapper.map(trainerRequestDto, Trainer.class);
        trainner.setPassword(passwordEncoder.encode(trainerRequestDto.getPassword()));
        return trainerRepository.save(trainner);
    }

    @Transactional
    public void deleteTrainer(Long id) {
        var trainer = trainerRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("Trainer not found with id: " + id));

        if (trainer.getPrograms() != null && !trainer.getPrograms().isEmpty())
            throw new InvalidOperationException("Cannot delete trainer with assigned programs");

        trainerRepository.delete(trainer);
    }

    @Transactional
    public TrainerResponseDto updateTrainer(Long id, TrainerRequestDto trainerRequestDto) {
        var trainer = trainerRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("Trainer not found with id: " + id));

        if (!trainer.getEmail().equals(trainerRequestDto.getEmail())) {
            if (trainerRepository.existsByEmail(trainerRequestDto.getEmail())) {
                throw new DuplicateResourceException(
                        "Email already in use: " + trainerRequestDto.getEmail());
            }
        }

        if (trainerRequestDto.getExperienceEndDate() != null &&
                trainerRequestDto.getExperienceEndDate().isBefore(trainerRequestDto.getExperienceStartDate()))
            throw new BadRequestException("End date cannot be before start date");

        modelMapper.map(trainerRequestDto, trainer);
        trainer.setPassword(passwordEncoder.encode(trainerRequestDto.getPassword()));

        return modelMapper.map(
                trainerRepository.save(trainer), TrainerResponseDto.class);
    }

    @Transactional
    public TrainerResponseDto updateTrainerStatus(Long id, UserStatus userStatus) {
        var trainer = trainerRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("Trainer not found with id: " + id));

        if (userStatus == null) {
            throw new BadRequestException("User status is required.");
        }

        trainer.setUserStatus(userStatus);

        return modelMapper.map(
                trainerRepository.save(trainer), TrainerResponseDto.class);
    }

    @Transactional
    public TrainerResponseDto updateTrainerExperience(Long id,
                                                      @Valid @RequestBody
                                                      TrainerExperienceDto trainerExperienceDto) {
        var trainer = trainerRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("Trainer not found with id: " + id));

        if (trainerExperienceDto.getExperienceStartDate() == null) {
            throw new BadRequestException("Experience start date is required");
        }

        if (trainerExperienceDto.getExperienceEndDate() != null &&
                trainerExperienceDto.getExperienceEndDate().isBefore(trainerExperienceDto.getExperienceStartDate())) {
            throw new BadRequestException("Experience end date cannot be before start date");
        }

        trainer.setExperienceStartDate(trainerExperienceDto.getExperienceStartDate());
        trainer.setExperienceEndDate(trainerExperienceDto.getExperienceEndDate());
        trainer.setExperienceTime(trainer.experienceTime());

        return modelMapper.map(
                trainerRepository.save(trainer), TrainerResponseDto.class);
    }
}