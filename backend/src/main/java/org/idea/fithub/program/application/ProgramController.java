package org.idea.fithub.program.application;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.idea.fithub.program.domain.ProgramService;
import org.idea.fithub.program.dto.ProgramRequestDto;
import org.idea.fithub.program.dto.ProgramResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/programs")
public class ProgramController {
    private final ProgramService programService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','TRAINER','LEARNER')")
    public ResponseEntity<ProgramResponseDto> createProgram(@Valid @RequestBody
                                                            ProgramRequestDto programRequestDto) {
        var program = programService.createProgram(programRequestDto);
        var location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(program.getId())
                .toUri();

        return ResponseEntity.created(location).body(program);
    }

    @PatchMapping("/{id}/assign/{routineId}")
    @PreAuthorize("hasAnyRole('ADMIN','TRAINER','LEARNER')")
    public ResponseEntity<ProgramResponseDto> assignRoutineToProgram(@PathVariable Long id,
                                                                     @PathVariable Long routineId) {
        return ResponseEntity.ok(
                programService.assignRoutineToProgram(id, routineId));
    }

    @GetMapping("/{learnerId}")
    @PreAuthorize("hasAnyRole('ADMIN','TRAINER')")
    public ResponseEntity<Page<ProgramResponseDto>> getProgramsByLearner(
            @PathVariable Long learnerId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return ResponseEntity.ok(
                programService.getProgramsByLearner(learnerId, PageRequest.of(page, size)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','TRAINER','LEARNER')")
    public ResponseEntity<Void> deleteProgram(@PathVariable Long id) {
        programService.deleteProgram(id);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','TRAINER','LEARNER')")
    public ResponseEntity<ProgramResponseDto> updateProgram(@RequestBody @Valid ProgramRequestDto programRequestDto,@PathVariable Long id) {
        var program = programService.updateProgram(id,programRequestDto);
        return ResponseEntity.ok(program);
    }

}
