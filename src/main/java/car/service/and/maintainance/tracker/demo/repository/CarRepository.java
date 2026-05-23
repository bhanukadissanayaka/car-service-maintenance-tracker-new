package car.service.and.maintainance.tracker.demo.repository;

import car.service.and.maintainance.tracker.demo.model.Car;
import car.service.and.maintainance.tracker.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {
    List<Car> findByUser(User user);
    List<Car> findByUserId(Long userId);
}
