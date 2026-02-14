package at.msm.asobo.repositories;

import at.msm.asobo.entities.Event;
import at.msm.asobo.entities.User;
import at.msm.asobo.entities.UserComment;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCommentRepository
    extends JpaRepository<UserComment, UUID>, JpaSpecificationExecutor<UserComment> {
  List<UserComment> findUserCommentsByCreationDate(LocalDateTime creationDate);

  List<UserComment> findUserCommentsByAuthor(User author);

  List<UserComment> findUserCommentsByEvent(Event event);

  List<UserComment> findUserCommentsByEventIdOrderByCreationDate(UUID eventId);

  Optional<UserComment> findUserCommentByEventIdAndId(UUID eventId, UUID commentId);

  @Query("SELECT c FROM UserComment c JOIN FETCH c.author JOIN FETCH c.event")
  Page<UserComment> findAllPageable(Pageable pageable);
}
