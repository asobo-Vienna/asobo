package at.msm.asobo.mappers;

import at.msm.asobo.dto.comment.UserCommentWithEventTitleDTO;
import at.msm.asobo.entities.UserComment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserCommentToUserCommentWithEventTitleDTOMapper {
  @Mapping(source = "author.username", target = "username")
  @Mapping(source = "author.id", target = "authorId")
  @Mapping(source = "author.pictureURI", target = "pictureURI")
  @Mapping(source = "event.id", target = "eventId")
  @Mapping(source = "event.title", target = "eventTitle")
  UserCommentWithEventTitleDTO toDTO(UserComment userComment);
}
