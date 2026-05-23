package car.service.and.maintainance.tracker.demo.repository;

import car.service.and.maintainance.tracker.demo.model.Role;
import car.service.and.maintainance.tracker.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    List<User> findByRole(Role role);
}
