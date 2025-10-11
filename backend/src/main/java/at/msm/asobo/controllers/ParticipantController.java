package at.msm.asobo.controllers;

import at.msm.asobo.dto.event.EventDTO;
import at.msm.asobo.dto.user.UserPublicDTO;
import at.msm.asobo.services.ParticipantService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/events/{eventId}/participants")
public class ParticipantController {

    private final ParticipantService participantService;

    public ParticipantController(ParticipantService participantService) {
        this.participantService = participantService;
    }

    @PostMapping()
    public UserPublicDTO addParticipantToEvent(@PathVariable UUID eventId, @RequestBody @Valid UserPublicDTO participantDTO) {
        return this.participantService.addParticipantToEvent(eventId, participantDTO.getId());
    }

    @GetMapping()
    public List<UserPublicDTO> getParticipantsByEventId(@PathVariable UUID eventId) {
        return this.participantService.getAllParticipantsByEventId(eventId);
    }
}
