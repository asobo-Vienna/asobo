package at.msm.asobo.mappers;

import at.msm.asobo.dto.comment.UserCommentDTO;
import at.msm.asobo.entities.Event;
import at.msm.asobo.entities.User;
import at.msm.asobo.entities.UserComment;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserCommentDTOUserCommentMapper {

    public UserCommentDTO mapUserCommentToUserCommentDTO(UserComment entity) {
        if (entity == null) {
            return null;
        }

        UserCommentDTO dto = new UserCommentDTO();
        dto.setId(entity.getId());
        dto.setText(entity.getText());
        dto.setCreationDate(entity.getCreationDate());
        dto.setModificationDate(entity.getModificationDate());
        dto.setPictureURI(entity.getPictureURI());

        if (entity.getAuthor() != null) {
            dto.setAuthorId(entity.getAuthor().getId());
            dto.setUsername(entity.getAuthor().getUsername());
        }

        if (entity.getEvent() != null) {
            dto.setEventId(entity.getEvent().getId());
        }
        return dto;
    }

    public UserComment mapUserCommentDTOToUserComment(UserCommentDTO dto, User author, Event event) {
        if (dto == null) {
            return null;
        }

        UserComment entity = new UserComment();
        entity.setId(dto.getId());
        entity.setText(dto.getText());
        entity.setAuthor(author);
        entity.setEvent(event);
        entity.setPictureURI(author.getPictureURI());
        entity.setCreationDate(dto.getCreationDate());
        entity.setModificationDate(dto.getModificationDate());
        return entity;
    }

    public List<UserCommentDTO> mapUserCommentsToUserCommentDTOs(List<UserComment> comments) {
        return comments.stream()
                .map(this::mapUserCommentToUserCommentDTO)
                .collect(Collectors.toList());
    }
}
