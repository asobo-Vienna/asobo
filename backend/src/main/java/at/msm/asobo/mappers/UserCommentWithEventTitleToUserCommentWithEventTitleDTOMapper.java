package at.msm.asobo.mappers;

import at.msm.asobo.dto.comment.UserCommentWithEventTitleDTO;
import at.msm.asobo.interfaces.UserCommentWithEventTitle;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring")
public interface UserCommentWithEventTitleToUserCommentWithEventTitleDTOMapper {
    UserCommentWithEventTitleDTO toDTO(UserCommentWithEventTitle userCommentWithEventTitle);
}
