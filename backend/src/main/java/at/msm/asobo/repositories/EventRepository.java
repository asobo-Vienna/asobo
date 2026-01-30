package at.msm.asobo.repositories;

import at.msm.asobo.entities.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {
    @Query("SELECT e FROM Event e")
    Page<Event> findAllEvents(Pageable pageable);

    List<Event> findByIsPrivateEventTrue();
    Page<Event> findByIsPrivateEventTrue(Pageable pageable);

    List<Event> findByIsPrivateEventFalse();
    Page<Event> findByIsPrivateEventFalse(Pageable pageable);

    // @Query("SELECT e.title FROM Event e WHERE e.id = :id")
    // String findEventTitleById(UUID id);

    List<Event> findEventsByDate(LocalDateTime date);

    List<Event> findEventsByDateBetween(LocalDateTime start, LocalDateTime end);

    List<Event> findEventsByLocation(String location);

    List<Event> findEventsByTitle(String title);

    List<Event> findByParticipants_Id(UUID userId);
    Page<Event> findByParticipants_Id(UUID userId, Pageable pageable);

    List<Event> findByParticipants_IdAndIsPrivateEventTrue(UUID userId);
    Page<Event> findByParticipants_IdAndIsPrivateEventTrue(UUID userId, Pageable pageable);

    // find public events attend by a certain user; underscore in method name is needed by JPA
    List<Event> findByParticipants_IdAndIsPrivateEventFalse(UUID userId);
    Page<Event> findByParticipants_IdAndIsPrivateEventFalse(UUID userId, Pageable pageable);

    @Query("SELECT DISTINCT e FROM Event e " +
            "LEFT JOIN e.creator c " +
            "WHERE (:includePrivate = true OR e.isPrivateEvent = false) " +
            "AND (:query IS NULL OR " +
            "  LOWER(e.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "  LOWER(e.description) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "  LOWER(e.location) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "  LOWER(c.username) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "AND (:startDate IS NULL OR e.date >= :startDate) " +
            "AND (:endDate IS NULL OR e.date <= :endDate) " +
            "AND (:location IS NULL OR LOWER(e.location) LIKE LOWER(CONCAT('%', :location, '%'))) " +
            "ORDER BY e.date ASC")
    List<Event> globalSearch(
            @Param("query") String query,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("location") String location,
            @Param("includePrivate") boolean includePrivate
    );
}
