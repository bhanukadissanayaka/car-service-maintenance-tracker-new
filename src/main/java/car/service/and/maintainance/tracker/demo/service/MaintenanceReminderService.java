package car.service.and.maintainance.tracker.demo.service;

import car.service.and.maintainance.tracker.demo.model.MaintenanceReminder;

import java.util.List;

public interface MaintenanceReminderService {
    MaintenanceReminder saveReminder(MaintenanceReminder reminder, Long carId);
    MaintenanceReminder getReminderById(Long id);
    List<MaintenanceReminder> getRemindersByCarId(Long carId);
    List<MaintenanceReminder> getActiveRemindersByUsername(String username);
    void checkAndTriggerAlerts(Long carId);
}
