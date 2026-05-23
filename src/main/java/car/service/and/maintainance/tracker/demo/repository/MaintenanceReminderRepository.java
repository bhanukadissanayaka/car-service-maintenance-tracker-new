package car.service.and.maintainance.tracker.demo.repository;

import car.service.and.maintainance.tracker.demo.model.MaintenanceReminder;
import car.service.and.maintainance.tracker.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaintenanceReminderRepository extends JpaRepository<MaintenanceReminder, Long> {
    List<MaintenanceReminder> findByCarIdAndStatus(Long carId, MaintenanceReminder.ReminderStatus status);
    List<MaintenanceReminder> findByCarUserAndStatus(User user, MaintenanceReminder.ReminderStatus status);
    List<MaintenanceReminder> findByCarId(Long carId);
}
