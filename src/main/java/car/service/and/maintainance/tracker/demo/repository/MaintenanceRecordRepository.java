package car.service.and.maintainance.tracker.demo.repository;

import car.service.and.maintainance.tracker.demo.model.MaintenanceRecord;
import car.service.and.maintainance.tracker.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaintenanceRecordRepository extends JpaRepository<MaintenanceRecord, Long> {
    List<MaintenanceRecord> findByCarIdOrderByServiceDateDesc(Long carId);
    List<MaintenanceRecord> findByCarUserOrderByServiceDateDesc(User user);
    List<MaintenanceRecord> findByAssignedEmployee(User employee);

    List<MaintenanceRecord> findByStatus(MaintenanceRecord.MaintenanceStatus status);
}
