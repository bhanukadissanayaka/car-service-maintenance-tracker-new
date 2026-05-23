package car.service.and.maintainance.tracker.demo.controller;

import car.service.and.maintainance.tracker.demo.model.Car;
import car.service.and.maintainance.tracker.demo.model.MaintenanceRecord;
import car.service.and.maintainance.tracker.demo.model.MaintenanceReminder;
import car.service.and.maintainance.tracker.demo.service.CarService;
import car.service.and.maintainance.tracker.demo.service.MaintenanceRecordService;
import car.service.and.maintainance.tracker.demo.service.MaintenanceReminderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final CarService carService;
    private final MaintenanceRecordService recordService;
    private final MaintenanceReminderService reminderService;

    @GetMapping("/")
    public String index(Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(Principal principal, Model model) {
        String username = principal.getName();
        
        List<Car> cars = carService.getCarsByUsername(username);
        List<MaintenanceRecord> records = recordService.getRecordsByUsername(username);
        List<MaintenanceReminder> reminders = reminderService.getActiveRemindersByUsername(username);

        // Aggregated Metrics
        int totalCars = cars.size();
        
        BigDecimal totalExpenses = records.stream()
                .filter(r -> r.getStatus() == MaintenanceRecord.MaintenanceStatus.COMPLETED)
                .map(MaintenanceRecord::getCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
                
        long pendingAlerts = reminders.stream()
                .filter(r -> r.getStatus() == MaintenanceReminder.ReminderStatus.PENDING)
                .count();

        long overdueAlerts = reminders.stream()
                .filter(r -> r.getStatus() == MaintenanceReminder.ReminderStatus.OVERDUE)
                .count();

        long scheduledTasks = records.stream()
                .filter(r -> r.getStatus() == MaintenanceRecord.MaintenanceStatus.SCHEDULED 
                          || r.getStatus() == MaintenanceRecord.MaintenanceStatus.IN_PROGRESS)
                .count();

        // Model attributes
        model.addAttribute("cars", cars);
        model.addAttribute("totalCars", totalCars);
        model.addAttribute("totalExpenses", totalExpenses);
        model.addAttribute("pendingAlertsCount", pendingAlerts);
        model.addAttribute("overdueAlertsCount", overdueAlerts);
        model.addAttribute("scheduledTasksCount", scheduledTasks);
        model.addAttribute("reminders", reminders);
        
        return "dashboard";
    }
}

