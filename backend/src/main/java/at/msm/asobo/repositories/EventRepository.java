package at.msm.asobo.repositories;

import at.msm.asobo.entities.Event;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository
    extends JpaRepository<Event, UUID>, JpaSpecificationExecutor<Event> {
  @Query("SELECT e FROM Event e")
  Page<Event> findAllEvents(Pageable pageable);

  @Query("SELECT e FROM Event e WHERE e.id = :id")
  Optional<Event> findById(UUID id);

  List<Event> findByIsPrivateEventTrue();

  Page<Event> findByIsPrivateEventTrue(Pageable pageable);

  List<Event> findByIsPrivateEventFalse();

  Page<Event> findByIsPrivateEventFalse(Pageable pageable);

  List<Event> findEventsByDate(LocalDateTime date);

  List<Event> findEventsByDateBetween(LocalDateTime start, LocalDateTime end);

  List<Event> findEventsByLocation(String location);

  List<Event> findEventsByTitle(String title);

  List<Event> findByParticipantsId(UUID userId);

  Page<Event> findByParticipantsId(UUID userId, Pageable pageable);

  List<Event> findByParticipantsIdAndIsPrivateEventTrue(UUID userId);

  Page<Event> findByParticipantsIdAndIsPrivateEventTrue(UUID userId, Pageable pageable);

  // find public events attend by a certain user; underscore in method name is needed by JPA
  List<Event> findByParticipantsIdAndIsPrivateEventFalse(UUID userId);

  Page<Event> findByParticipantsIdAndIsPrivateEventFalse(UUID userId, Pageable pageable);

  @Query(
      value =
          """
                            SELECT DISTINCT e.*
                            FROM event e
                            WHERE
                              (:includePrivate = true OR e.is_private = false)

                              AND (
                                :query IS NULL
                                OR LOWER(e.title) LIKE LOWER(CONCAT('%', :query, '%'))
                                OR LOWER(e.description) LIKE LOWER(CONCAT('%', :query, '%'))
                                OR LOWER(e.location) LIKE LOWER(CONCAT('%', :query, '%'))
                              )

                              AND e.date >= COALESCE(:startDate, e.date)
                              AND e.date <= COALESCE(:endDate, e.date)

                              AND LOWER(e.location) LIKE LOWER(CONCAT('%', COALESCE(:location, ''), '%'))

                            ORDER BY e.date ASC
                            """,
      nativeQuery = true)
  List<Event> globalSearch(
      @Param("query") String query,
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate,
      @Param("location") String location,
      @Param("includePrivate") boolean includePrivate);
}
