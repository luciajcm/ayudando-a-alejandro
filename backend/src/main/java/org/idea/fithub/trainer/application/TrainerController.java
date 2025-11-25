package org.idea.fithub.trainer.application;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.idea.fithub.trainer.domain.TrainerService;
import org.idea.fithub.trainer.dto.TrainerExperienceDto;
import org.idea.fithub.trainer.dto.TrainerRequestDto;
import org.idea.fithub.trainer.dto.TrainerResponseDto;
import org.idea.fithub.user.domain.UserStatus;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/trainers")
public class TrainerController {
    private final TrainerService trainerService;
    private final ModelMapper modelMapper;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LEARNER','TRAINER')")
    public ResponseEntity<Page<TrainerResponseDto>> getAllTrainers(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return ResponseEntity.ok(
                trainerService.getAllTrainers(PageRequest.of(page, size)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LEARNER','TRAINER')")
    public ResponseEntity<TrainerResponseDto> getTrainer(@PathVariable Long id) {
        return ResponseEntity.ok(
                trainerService.getTrainer(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TrainerResponseDto> createTrainer(@Valid @RequestBody
                                                 TrainerRequestDto trainerRequestDto) {
        var trainer = trainerService.createTrainer(trainerRequestDto);
        var location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(trainer.getId())
                .toUri();

        var responseDto = modelMapper.map(trainer, TrainerResponseDto.class);


        return ResponseEntity.created(location).body(responseDto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','TRAINER')")
    public ResponseEntity<Void> deleteTrainer(@PathVariable Long id) {
        trainerService.deleteTrainer(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','TRAINER')")
    public ResponseEntity<TrainerResponseDto> updateTrainer(@PathVariable Long id,
                                                            @Valid @RequestBody
                                                            TrainerRequestDto trainerRequestDto) {
        return ResponseEntity.ok(
                trainerService.updateTrainer(id, trainerRequestDto));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN','TRAINER')")
    public ResponseEntity<TrainerResponseDto> updateTrainerStatus(@PathVariable Long id,
                                                                  @RequestParam UserStatus userStatus) {
        return ResponseEntity.ok(
                trainerService.updateTrainerStatus(id, userStatus));
    }

    @PatchMapping("/{id}/experience")
    @PreAuthorize("hasAnyRole('ADMIN','TRAINER')")
    public ResponseEntity<TrainerResponseDto> updateTrainerExperience(@PathVariable Long id,
                                                                      @Valid @RequestBody
                                                                      TrainerExperienceDto trainerExperienceDto) {
        return ResponseEntity.ok(
                trainerService.updateTrainerExperience(id, trainerExperienceDto));
    }
}