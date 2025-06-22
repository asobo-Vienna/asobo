package at.msm.asobo.mappers;

import at.msm.asobo.dto.medium.MediumCreationDTO;
import at.msm.asobo.dto.medium.MediumDTO;
import at.msm.asobo.entities.Medium;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;


@Mapper(componentModel = "spring")
public interface MediumDTOMediumMapper {

    @Mapping(source = "event.id", target = "eventId")
    MediumDTO mapMediumToMediumDTO(Medium medium);

    Medium mapMediumDTOToMedium(MediumDTO mediumDTO);

    List<MediumDTO> mapMediaToMediaDTOList(List<Medium> media);

    @Mapping(target = "mediumFile", ignore = true)
    MediumCreationDTO mapMediumToMediumCreationDTO(Medium medium);

    @Mapping(target = "mediumURI", ignore = true)
    Medium mapMediumCreationDTOToMedium(MediumCreationDTO mediumCreationDTO);
    List<MediumCreationDTO> mapMediaToMediaCreationDTOList(List<MediumCreationDTO> mediaCreationDTO);
    List<Medium> mapMediaCreationDTOToMediaList(List<MediumCreationDTO> mediaCreationDTO);
}
