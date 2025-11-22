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

    @Query("SELECT c as comment, e.title as eventTitle " +
            "FROM UserComment c JOIN c.event e")
    List<UserCommentWithEventTitle> findAllCommentsWithEventTitles();
}
