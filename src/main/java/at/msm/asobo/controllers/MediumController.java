package at.msm.asobo.controllers;

import at.msm.asobo.entities.media.Medium;
import at.msm.asobo.services.MediumService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/events/{eventID}/media")
public class MediumController {

   private final MediumService mediumService;

    public MediumController(MediumService mediumService) {
        this.mediumService = mediumService;
    }


    @GetMapping()
    public List<Medium> getAllMedia(@PathVariable UUID eventID) {
        return mediumService.getAllMediaByEventId(eventID);
    }


    @GetMapping("/{mediumID}")
    public Medium getMediumById(@PathVariable UUID eventID, @PathVariable UUID mediumID) {
        return mediumService.getMediumByEventIdAndMediumId(eventID, mediumID);
    }

    @PostMapping
    public Medium addMedium(@PathVariable UUID eventID, @RequestBody @Valid Medium medium) {
        return this.mediumService.saveMedium(eventID, medium);
    }


    @DeleteMapping("/{mediumID}")
    public Medium deleteMediumById(@PathVariable UUID eventID, @PathVariable UUID mediumID) {
        return mediumService.deleteMediumById(eventID, mediumID);
    }
}
