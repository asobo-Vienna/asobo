package at.msm.asobo.repositories;

import at.msm.asobo.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
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

    @Query("SELECT u FROM User u WHERE " +
            "u.isActive = true AND (" +
            "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(u.surname) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(CONCAT(u.firstName, ' ', u.surname)) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(u.username) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(u.aboutMe) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(u.location) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<User> searchUsers(@Param("query") String query);
}
