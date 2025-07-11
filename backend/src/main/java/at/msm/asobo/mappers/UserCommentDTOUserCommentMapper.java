package at.msm.asobo.mappers;

import at.msm.asobo.dto.comment.UserCommentDTO;
import at.msm.asobo.entities.UserComment;
import at.msm.asobo.mappers.helpers.EventMapperHelper;
import at.msm.asobo.mappers.helpers.UserMapperHelper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapperHelper.class, EventMapperHelper.class})
public interface UserCommentDTOUserCommentMapper {

    @Mapping(source = "author", target = "authorId", qualifiedByName = "userToUuid")
    @Mapping(source = "event", target = "eventId", qualifiedByName = "eventToUuid")
    UserCommentDTO mapUserCommentToUserCommentDTO(UserComment userComment);

    @Mapping(source = "authorId", target = "author", qualifiedByName = "uuidToUser")
    @Mapping(source = "eventId", target = "event", qualifiedByName = "uuidToEvent")
    UserComment mapUserCommentDTOToUserComment(UserCommentDTO userCommentDTO);

    List<UserCommentDTO> mapUserCommentsToUserCommentDTOs(List<UserComment> userComments);
    List<UserComment> mapUserCommentDTOsToUserComments(List<UserCommentDTO> userCommentDTOs);
}
