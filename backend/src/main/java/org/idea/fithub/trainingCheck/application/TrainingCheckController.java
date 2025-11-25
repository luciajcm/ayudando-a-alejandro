package org.idea.fithub.trainingCheck.application;

import lombok.RequiredArgsConstructor;
import org.idea.fithub.trainingCheck.domain.TrainingCheckService;
import org.idea.fithub.trainingCheck.dto.TrainingCheckResponseDto;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/learners")
public class TrainingCheckController {
    private final TrainingCheckService trainingCheckService;

    @PostMapping("/{learnerId}/check")
    @PreAuthorize("hasAnyRole('ADMIN', 'LEARNER')")
    public ResponseEntity<String> markCheck(@PathVariable Long learnerId,
                                            @DateTimeFormat(pattern = "dd/MM/yyyy")
                                            @RequestParam LocalDate date) {
        trainingCheckService.markCheck(learnerId, date);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{learnerId}/check")
    @PreAuthorize("hasAnyRole('ADMIN', 'LEARNER')")
    public ResponseEntity<String> unmarkCheck(@PathVariable Long learnerId,
                                              @DateTimeFormat(pattern = "dd/MM/yyyy")
                                              @RequestParam LocalDate date) {
        trainingCheckService.unmarkCheck(learnerId, date);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("{learnerId}/checks")
    @PreAuthorize("hasAnyRole('ADMIN', 'LEARNER')")
    public ResponseEntity<TrainingCheckResponseDto> getTrainingCheck(@PathVariable Long learnerId) {
        return ResponseEntity.ok(
                trainingCheckService.getTrainingCheck(learnerId));
    }
}
