package nl.leomoot.springsociallogin.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import nl.leomoot.springsociallogin.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByEmail(final String email);

  boolean existsByEmail(final String email);
}
