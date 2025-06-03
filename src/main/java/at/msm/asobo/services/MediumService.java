package at.msm.asobo.services;

import at.msm.asobo.entities.Event;
import at.msm.asobo.entities.media.Medium;
import at.msm.asobo.exceptions.EventNotFoundException;
import at.msm.asobo.exceptions.MediumNotFoundException;
import at.msm.asobo.repositories.EventRepository;
import at.msm.asobo.repositories.MediumRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class MediumService {

    private final MediumRepository mediumRepository;
    private final EventRepository eventRepository;

    public MediumService(MediumRepository mediumRepository, EventRepository eventRepository) {
        this.mediumRepository = mediumRepository;
        this.eventRepository = eventRepository;
    }


    public List<Medium> getAllMediaByEventId(UUID eventId) {
        return this.mediumRepository.findMediaByEventId(eventId);
    }


    public Medium getMediumByEventIdAndMediumId(UUID eventID, UUID id) {
        return this.mediumRepository.findMediumByEventIdAndId(eventID, id)
                                    .orElseThrow(() -> new MediumNotFoundException(id));
    }


    public Medium saveMedium(UUID eventID, Medium medium) {
        Event event = eventRepository.findById(eventID)
                .orElseThrow(() -> new EventNotFoundException(eventID));

        medium.setEvent(event);

        return this.mediumRepository.save(medium);
    }


    public Medium deleteMediumById(UUID eventID, UUID id) {
        Medium medium = this.getMediumByEventIdAndMediumId(eventID, id);
        mediumRepository.delete(medium);
        return medium;
    }

    // TODO update Media???
}
