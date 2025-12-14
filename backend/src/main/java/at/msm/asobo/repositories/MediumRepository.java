package at.msm.asobo.repositories;

import at.msm.asobo.entities.Medium;
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

    @Query("SELECT m FROM Medium m JOIN FETCH m.event")
    Page<Medium> findAllPageable(Pageable pageable);
}
