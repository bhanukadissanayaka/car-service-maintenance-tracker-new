package car.service.and.maintainance.tracker.demo.service;

import car.service.and.maintainance.tracker.demo.model.MaintenanceRecord;

import java.math.BigDecimal;
import java.util.List;

public interface MaintenanceRecordService {
    MaintenanceRecord scheduleService(MaintenanceRecord record, Long carId);
    MaintenanceRecord getRecordById(Long id);
    List<MaintenanceRecord> getRecordsByCarId(Long carId);
    List<MaintenanceRecord> getRecordsByUsername(String username);
    List<MaintenanceRecord> getUnassignedRecords();
    List<MaintenanceRecord> getRecordsByEmployeeUsername(String employeeUsername);
    void claimJob(Long recordId, String employeeUsername);
    void completeJob(Long recordId, String notes, BigDecimal cost, Integer serviceMileage);
    void deleteRecord(Long id);
}
