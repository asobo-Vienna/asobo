package at.msm.asobo.services;

import at.msm.asobo.dto.medium.MediumCreationDTO;
import at.msm.asobo.dto.medium.MediumDTO;
import at.msm.asobo.entities.Event;
import at.msm.asobo.entities.Medium;
import at.msm.asobo.entities.User;
import at.msm.asobo.exceptions.MediumNotFoundException;
import at.msm.asobo.mappers.MediumDTOMediumMapper;
import at.msm.asobo.repositories.MediumRepository;
import at.msm.asobo.security.UserPrincipal;
import at.msm.asobo.services.events.EventService;
import at.msm.asobo.services.files.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class MediumService {


    private final MediumRepository mediumRepository;
    private final MediumDTOMediumMapper mediumDTOMediumMapper;
    private final EventService eventService;
    private final UserService userService;
    private final AccessControlService accessControlService;
    private final FileStorageService fileStorageService;

    @Value("${app.file-storage.event-galleries-subfolder}")
    private String eventMediaSubfolder;

    public MediumService(MediumRepository mediumRepository,
                         MediumDTOMediumMapper mediumDTOMediumMapper,
                         EventService eventService,
                         UserService userService,
                         AccessControlService accessControlService,
                         FileStorageService fileStorageService) {
        this.mediumRepository = mediumRepository;
        this.mediumDTOMediumMapper = mediumDTOMediumMapper;
        this.eventService = eventService;
        this.userService = userService;
        this.accessControlService = accessControlService;
        this.fileStorageService = fileStorageService;
    }


    public List<MediumDTO> getAllMediaByEventId(UUID eventId) {
        List<Medium> media = this.mediumRepository.findMediaByEventId(eventId);
        return this.mediumDTOMediumMapper.mapMediaToMediaDTOList(media);
    }


    public Medium getMediumByIdAndEventId(UUID mediumId, UUID eventId) {
        Medium medium = this.mediumRepository.findMediumByIdAndEventId(mediumId, eventId)
                .orElseThrow(() -> new MediumNotFoundException(mediumId));
        return medium;
    }

    public MediumDTO getMediumDTOByIdAndEventId(UUID mediumId, UUID eventId) {
        Medium medium = this.getMediumByIdAndEventId(mediumId, eventId);
        return this.mediumDTOMediumMapper.mapMediumToMediumDTO(medium);
    }


    public MediumDTO addMediumToEventById(UUID eventId,
                                          MediumCreationDTO creationDTO,
                                          UserPrincipal userPrincipal) {
        Event event = eventService.getEventById(eventId);
        User loggedInUser = this.userService.getUserById(userPrincipal.getUserId());

        this.accessControlService.assertCanUploadMedia(event, loggedInUser);

        Medium newMedium = this.mediumDTOMediumMapper.mapMediumCreationDTOToMedium(creationDTO);
        newMedium.setEvent(event);
        newMedium.setCreator(loggedInUser);

        if (creationDTO.getMediumFile() != null && !creationDTO.getMediumFile().isEmpty()) {
            String fileURI = fileStorageService.store(creationDTO.getMediumFile(), this.eventMediaSubfolder + "/" + eventId);
            newMedium.setMediumURI(fileURI);
        }

        Medium savedMedium = this.mediumRepository.save(newMedium);
        return this.mediumDTOMediumMapper.mapMediumToMediumDTO(savedMedium);
    }


    public MediumDTO deleteMediumById(UUID mediumId,
                                      UUID eventId,
                                      UserPrincipal userPrincipal) {

        Medium mediumToDelete = this.getMediumByIdAndEventId(mediumId, eventId);
        User loggedInUser = this.userService.getUserById(userPrincipal.getUserId());

        this.accessControlService.assertCanDeleteMedium(mediumToDelete, loggedInUser);

        this.fileStorageService.delete(mediumToDelete.getMediumURI());
        mediumRepository.delete(mediumToDelete);
        return this.mediumDTOMediumMapper.mapMediumToMediumDTO(mediumToDelete);
    }

    // TODO update Media???
}
