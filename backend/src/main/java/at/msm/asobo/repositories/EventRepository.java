package at.msm.asobo.repositories;

import at.msm.asobo.entities.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {
    List<Event> findByIsPrivateEventTrue();

    List<Event> findByIsPrivateEventFalse();

    // @Query("SELECT e.title FROM Event e WHERE e.id = :id")
    // String findEventTitleById(UUID id);

    List<Event> findEventsByDate(LocalDateTime date);

    List<Event> findEventsByDateBetween(LocalDateTime start, LocalDateTime end);

    List<Event> findEventsByLocation(String location);

    List<Event> findEventsByTitle(String title);

    List<Event> findByParticipants_Id(UUID userId);

    List<Event> findByParticipants_IdAndIsPrivateEventTrue(UUID userId);

    // find public events attend by a certain user; underscore in method name is needed by JPA
    List<Event> findByParticipants_IdAndIsPrivateEventFalse(UUID userId);
}
