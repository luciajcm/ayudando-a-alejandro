package org.idea.fithub.learner.application;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.idea.fithub.learner.domain.LearnerService;
import org.idea.fithub.learner.dto.LearnerRequestDto;
import org.idea.fithub.learner.dto.LearnerResponseDto;
import org.idea.fithub.learner.dto.LearnerStatsDto;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/learners")
public class LearnerController {
    private final LearnerService learnerService;
    private final ModelMapper modelMapper;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','TRAINER')")
    public ResponseEntity<Page<LearnerResponseDto>> getAllLearners(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return ResponseEntity.ok(
                learnerService.getAllLearners(PageRequest.of(page, size)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','TRAINER')")
    public ResponseEntity<LearnerResponseDto> getLearner(@PathVariable Long id) {
        return ResponseEntity.ok(
                learnerService.getLearner(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','TRAINER','LEARNER')")
    public ResponseEntity<LearnerResponseDto> createLearner(@Valid @RequestBody
                                                 LearnerRequestDto learnerRequestDto) {
        var learner = learnerService.createLearner(learnerRequestDto);
        var location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(learner.getId())
                .toUri();

        var responseDto = modelMapper.map(learner, LearnerResponseDto.class);

        return ResponseEntity.created(location).body(responseDto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','LEARNER')")
    public ResponseEntity<Void> deleteLearner(@PathVariable Long id) {
        learnerService.deleteLearner(id);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','LEARNER')")
    public ResponseEntity<LearnerResponseDto> updateLearner(@PathVariable Long id,
                                                            @Valid @RequestBody
                                                            LearnerRequestDto learnerRequestDto) {
        return ResponseEntity.ok(
                learnerService.updateLearner(id, learnerRequestDto));
    }

    @PatchMapping("/{id}/stats")
    @PreAuthorize("hasAnyRole('ADMIN','LEARNER')")
    public ResponseEntity<LearnerResponseDto> updateLearnerStats(@PathVariable Long id,
                                                                 @Valid @RequestBody
                                                                 LearnerStatsDto learnerStatsDto) {
        return ResponseEntity.ok(
                learnerService.updateLearnerStats(id, learnerStatsDto));
    }
}