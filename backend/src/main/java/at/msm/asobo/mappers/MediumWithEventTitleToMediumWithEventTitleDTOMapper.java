package at.msm.asobo.mappers;

import at.msm.asobo.dto.medium.MediumWithEventTitleDTO;
import at.msm.asobo.interfaces.MediumWithEventTitle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring", uses = {MediumDTOMediumMapper.class})
public interface MediumWithEventTitleToMediumWithEventTitleDTOMapper {
    @Mapping(target = "mediumDTO", source = "medium")
    MediumWithEventTitleDTO toDTO(MediumWithEventTitle mediumWithEventTitle);
    List<MediumWithEventTitleDTO> toDTOList(List<MediumWithEventTitle> mediaWithEventTitles);
}
