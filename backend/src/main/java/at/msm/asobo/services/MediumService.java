package at.msm.asobo.services;

import at.msm.asobo.dto.medium.MediumCreationDTO;
import at.msm.asobo.dto.medium.MediumDTO;
import at.msm.asobo.entities.Event;
import at.msm.asobo.entities.Medium;
import at.msm.asobo.exceptions.MediumNotFoundException;
import at.msm.asobo.mappers.MediumDTOMediumMapper;
import at.msm.asobo.repositories.MediumRepository;
import at.msm.asobo.services.files.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class MediumService {


    private final MediumRepository mediumRepository;
    private final EventService eventService;
    private final MediumDTOMediumMapper mediumDTOMediumMapper;
    private final FileStorageService fileStorageService;

    @Value("${app.file-storage.event-galleries-subfolder}")
    private String eventMediaSubfolder;

    public MediumService(MediumRepository mediumRepository,
                         EventService eventService,
                         MediumDTOMediumMapper mediumDTOMediumMapper,
                         FileStorageService fileStorageService) {
        this.mediumRepository = mediumRepository;
        this.eventService = eventService;
        this.mediumDTOMediumMapper = mediumDTOMediumMapper;
        this.fileStorageService = fileStorageService;
    }


    public List<MediumDTO> getAllMediaByEventId(UUID eventId) {
        List<Medium> media = this.mediumRepository.findMediaByEventId(eventId);
        return this.mediumDTOMediumMapper.mapMediaToMediaDTOList(media);
    }


    public Medium getMediumByEventIdAndMediumId(UUID eventID, UUID id) {
        Medium medium = this.mediumRepository.findMediumByEventIdAndId(eventID, id)
                .orElseThrow(() -> new MediumNotFoundException(id));
        return medium;
    }

    public MediumDTO getMediumDTOByEventIdAndMediumId(UUID eventID, UUID id) {
        Medium medium = this.getMediumByEventIdAndMediumId(eventID, id);
        return this.mediumDTOMediumMapper.mapMediumToMediumDTO(medium);
    }


    public MediumDTO addMediumToEventById(UUID eventID, MediumCreationDTO creationDTO) {
        Event event = eventService.getEventById(eventID);
        Medium newMedium = this.mediumDTOMediumMapper.mapMediumCreationDTOToMedium(creationDTO);
        newMedium.setEvent(event);

        if (creationDTO.getMediumFile() != null && !creationDTO.getMediumFile().isEmpty()) {
            String fileURI = fileStorageService.store(creationDTO.getMediumFile(), this.eventMediaSubfolder + "/" + eventID);
            newMedium.setMediumURI(fileURI);
        }

        Medium savedMedium = this.mediumRepository.save(newMedium);
        return this.mediumDTOMediumMapper.mapMediumToMediumDTO(savedMedium);
    }


    public MediumDTO deleteMediumById(UUID eventID, UUID id) {
        Medium mediumToDelete = this.getMediumByEventIdAndMediumId(eventID, id);
        this.fileStorageService.delete(mediumToDelete.getMediumURI());
        mediumRepository.delete(mediumToDelete);
        return this.mediumDTOMediumMapper.mapMediumToMediumDTO(mediumToDelete);
    }

    // TODO update Media???
}
