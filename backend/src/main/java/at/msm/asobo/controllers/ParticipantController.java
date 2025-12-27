package at.msm.asobo.controllers;

import at.msm.asobo.dto.user.UserPublicDTO;
import at.msm.asobo.services.ParticipantService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/events/{eventId}/participants")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN', 'USER')")
public class ParticipantController {

    private final ParticipantService participantService;

    public ParticipantController(ParticipantService participantService) {
        this.participantService = participantService;
    }

    // returns updated participants list
    @PostMapping()
    public List<UserPublicDTO> toggleParticipantInEvent(@PathVariable UUID eventId, @RequestBody @Valid UserPublicDTO participantDTO) {
        return this.participantService.toggleParticipantInEvent(eventId, participantDTO);
    }

    @GetMapping()
    public List<UserPublicDTO> getParticipantsByEventId(@PathVariable UUID eventId) {
        return this.participantService.getAllParticipantsAsDTOsByEventId(eventId);
    }
}
