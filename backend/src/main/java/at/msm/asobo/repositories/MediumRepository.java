package at.msm.asobo.repositories;

import at.msm.asobo.entities.Medium;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MediumRepository extends JpaRepository<Medium, UUID> {

    public List<Medium> findMediaByEventId(UUID eventId);

    Optional<Medium> findMediumByEventIdAndId(UUID eventId, UUID mediumId);
}
