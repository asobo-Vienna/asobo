package at.msm.asobo.mappers;

import at.msm.asobo.dto.medium.MediumWithEventTitleDTO;
import at.msm.asobo.entities.Medium;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MediumToMediumWithEventTitleDTOMapper {
    @Mapping(source = "event.id", target = "eventId")
    @Mapping(source = "event.title", target = "eventTitle")
    MediumWithEventTitleDTO toDTO(Medium medium);
}
