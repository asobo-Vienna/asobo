package at.msm.asobo.repositories;

import at.msm.asobo.entities.User;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    public Optional<User> findUserById(UUID id);

    public Optional<User> findByUsername(String username);

    public Optional<User> findByEmail(String email);

    public Optional<User> findByEmailOrUsername(String email, String username);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
