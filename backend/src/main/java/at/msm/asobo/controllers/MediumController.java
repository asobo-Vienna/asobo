package at.msm.asobo.controllers;

import at.msm.asobo.dto.medium.MediumCreationDTO;
import at.msm.asobo.dto.medium.MediumDTO;
import at.msm.asobo.services.MediumService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/events/{eventID}/media")
public class MediumController {

   private final MediumService mediumService;

    public MediumController(MediumService mediumService) {
        this.mediumService = mediumService;
    }

    @GetMapping()
    public List<MediumDTO> getAllMedia(@PathVariable UUID eventID) {
        return mediumService.getAllMediaByEventId(eventID);
    }

    @GetMapping("/{mediumID}")
    public MediumDTO getMediumById(@PathVariable UUID eventID, @PathVariable UUID mediumID) {
        return this.mediumService.getMediumDTOByEventIdAndMediumId(eventID, mediumID);
    }

    @PostMapping
    public MediumDTO addMediumToEventById(@PathVariable UUID eventID, @RequestBody @Valid MediumCreationDTO medium) {
        return this.mediumService.addMediumToEventById(eventID, medium);
    }

    @DeleteMapping("/{mediumID}")
    public MediumDTO deleteMediumById(@PathVariable UUID eventID, @PathVariable UUID mediumID) {
        return this.mediumService.deleteMediumById(eventID, mediumID);
    }
}
