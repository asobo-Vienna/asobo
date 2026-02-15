package at.msm.asobo.repositories;

import at.msm.asobo.entities.Medium;
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
public interface MediumRepository
    extends JpaRepository<Medium, UUID>, JpaSpecificationExecutor<Medium> {

  List<Medium> findAll();

  List<Medium> findMediaByEventId(UUID eventId);

  Optional<Medium> findMediumByIdAndEventId(UUID mediumId, UUID eventId);

  @Query("SELECT m FROM Medium m JOIN FETCH m.event")
  Page<Medium> findAllPageable(Pageable pageable);
}
