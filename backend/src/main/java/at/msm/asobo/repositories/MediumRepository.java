package at.msm.asobo.repositories;

import at.msm.asobo.entities.Medium;
import at.msm.asobo.interfaces.MediumWithEventTitle;
import at.msm.asobo.interfaces.UserCommentWithEventTitle;
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

    @Query("SELECT m as medium, e.title as eventTitle " +
            "FROM Medium m JOIN m.event e")
    List<MediumWithEventTitle> findAllMediaWithEventTitles();
}
