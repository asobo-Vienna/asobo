package at.msm.asobo.repositories;

import at.msm.asobo.entities.User;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {

  Optional<User> findUserById(UUID id);

  Optional<User> findByUsername(String username);

  Optional<User> findByEmail(String email);

  Optional<User> findByEmailOrUsername(String email, String username);

  Set<User> findAllByIdIn(Set<UUID> ids);

  boolean existsByUsername(String username);

  boolean existsByEmail(String email);

  @Query(
      """
            SELECT u FROM User u
            WHERE :query IS NOT NULL
              AND TRIM(:query) <> ''
              AND (
                LOWER(u.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR
                LOWER(u.surname) LIKE LOWER(CONCAT('%', :query, '%')) OR
                LOWER(CONCAT(u.firstName, ' ', u.surname)) LIKE LOWER(CONCAT('%', :query, '%')) OR
                LOWER(u.username) LIKE LOWER(CONCAT('%', :query, '%'))
              )
            """)
  List<User> searchUsers(@Param("query") String query);
}
