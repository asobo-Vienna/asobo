package at.msm.asobo.mapper;

import at.msm.asobo.dto.comment.UserCommentDTO;
import at.msm.asobo.dto.user.UserDTO;
import at.msm.asobo.entities.User;
import at.msm.asobo.entities.UserComment;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserCommentDTOUserCommentMapper {
    UserCommentDTO mapUserCommentToUserCommentDTO(UserComment userComment);
    UserComment mapUserCommentDTOToUserComment(UserCommentDTO userCommentDTO);

    List<UserCommentDTO> mapUserCommentssToUserCommentDTOs(List<UserComment> userComments);
    List<UserComment> mapUserCommentDTOsToUserComments(List<UserCommentDTO> userCommentDTOs);
}
