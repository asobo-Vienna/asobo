package at.msm.asobo.mappers;

import at.msm.asobo.dto.medium.MediumWithEventTitleDTO;
import at.msm.asobo.interfaces.MediumWithEventTitle;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MediumWithEventTitleToMediumWithEventTitleDTOMapper {
    MediumWithEventTitleDTO toDTO(MediumWithEventTitle mediumWithEventTitle);
}
