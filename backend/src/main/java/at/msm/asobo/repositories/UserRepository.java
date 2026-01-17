package at.msm.asobo.repositories;

import at.msm.asobo.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findUserById(UUID id);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailOrUsername(String email, String username);

    Set<User> findAllByIdIn(Set<UUID> ids);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
