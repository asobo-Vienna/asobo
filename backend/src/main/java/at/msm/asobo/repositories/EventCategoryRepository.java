package at.msm.asobo.repositories;

import at.msm.asobo.entities.EventCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface EventCategoryRepository
    extends JpaRepository<EventCategory, Long>, JpaSpecificationExecutor<EventCategory> {}
