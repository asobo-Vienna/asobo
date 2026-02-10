package at.msm.asobo.repositories;

import at.msm.asobo.entities.Role;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
  List<Role> findAllBy();

  Optional<Role> findByName(String name);
}
