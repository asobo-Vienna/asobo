package at.msm.asobo.repositories;

import at.msm.asobo.entities.Event;
import at.msm.asobo.entities.User;
import at.msm.asobo.entities.UserComment;
import at.msm.asobo.interfaces.UserCommentWithEventTitle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserCommentRepository extends JpaRepository<UserComment, UUID> {
    List<UserComment> findUserCommentsByCreationDate(LocalDateTime creationDate);

    List<UserComment> findUserCommentsByAuthor(User author);

    List<UserComment> findUserCommentsByEvent(Event event);

    List<UserComment> findUserCommentsByEventIdOrderByCreationDate(UUID eventId);

    Optional<UserComment> findUserCommentByEventIdAndId(UUID eventId, UUID commentId);

    @Query("SELECT c.id as id, c.text as text, c.author.username as username, " +
            "c.author.id as authorId, c.event.id as eventId, c.author.pictureURI as pictureURI, " +
            "c.creationDate as creationDate, c.modificationDate as modificationDate, " +
            "c.event.title as eventTitle " +
            "FROM UserComment c")
    List<UserCommentWithEventTitle> findAllCommentsWithEventTitle();
}
