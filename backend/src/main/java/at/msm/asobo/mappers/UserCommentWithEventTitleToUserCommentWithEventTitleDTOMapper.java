package at.msm.asobo.mappers;

import at.msm.asobo.dto.comment.UserCommentWithEventTitleDTO;
import at.msm.asobo.interfaces.UserCommentWithEventTitle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserCommentDTOUserCommentMapper.class})
public interface UserCommentWithEventTitleToUserCommentWithEventTitleDTOMapper {
    @Mapping(target = "userCommentDTO", source = "comment")
    UserCommentWithEventTitleDTO toDTO(UserCommentWithEventTitle userCommentWithEventTitle);
    List<UserCommentWithEventTitleDTO> toDTOList(List<UserCommentWithEventTitle> userCommentsWithEventTitles);
}
