package at.msm.asobo.repositories;

import at.msm.asobo.entities.Medium;
import at.msm.asobo.interfaces.MediumWithEventTitle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MediumRepository extends JpaRepository<Medium, UUID> {

    List<Medium> findAll();

    List<Medium> findMediaByEventId(UUID eventId);

    Optional<Medium> findMediumByEventIdAndId(UUID eventId, UUID mediumId);

    @Query("SELECT m.id as id, m.event.id as eventId, m.mediumURI as mediumURI, " +
            "m.event.title as eventTitle " +
            "FROM Medium m")
    Page<MediumWithEventTitle> findAllMediaWithEventTitle(Pageable pageable);
}
