package car.service.and.maintainance.tracker.demo.controller;

import car.service.and.maintainance.tracker.demo.model.MaintenanceRecord;
import car.service.and.maintainance.tracker.demo.model.MaintenanceRecord.MaintenanceStatus;
import car.service.and.maintainance.tracker.demo.service.MaintenanceRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class EmployeeController {

    private final MaintenanceRecordService recordService;

    @GetMapping("/employee/dashboard")
    public String employeeDashboard(Principal principal, Model model) {
        String username = principal.getName();
        
        List<MaintenanceRecord> queue = recordService.getUnassignedRecords();
        List<MaintenanceRecord> allMyJobs = recordService.getRecordsByEmployeeUsername(username);

        // Separate active tasks from completed history
        List<MaintenanceRecord> activeTasks = allMyJobs.stream()
                .filter(r -> r.getStatus() == MaintenanceStatus.IN_PROGRESS)
                .collect(Collectors.toList());

        List<MaintenanceRecord> completedHistory = allMyJobs.stream()
                .filter(r -> r.getStatus() == MaintenanceStatus.COMPLETED)
                .collect(Collectors.toList());

        model.addAttribute("queue", queue);
        model.addAttribute("activeTasks", activeTasks);
        model.addAttribute("completedHistory", completedHistory);
        model.addAttribute("myJobsCount", activeTasks.size());
        model.addAttribute("completedCount", completedHistory.size());
        model.addAttribute("unassignedCount", queue.size());

        return "employee-dashboard";
    }

    @PostMapping("/employee/jobs/{id}/claim")
    public String claimJob(@PathVariable("id") Long id, Principal principal) {
        String username = principal.getName();
        try {
            recordService.claimJob(id, username);
            return "redirect:/employee/dashboard?claimed";
        } catch (Exception e) {
            return "redirect:/employee/dashboard?error=" + e.getMessage();
        }
    }

    @PostMapping("/employee/jobs/{id}/complete")
    public String completeJob(
            @PathVariable("id") Long id,
            @RequestParam("notes") String notes,
            @RequestParam("cost") BigDecimal cost,
            @RequestParam("serviceMileage") Integer serviceMileage) {
        try {
            recordService.completeJob(id, notes, cost, serviceMileage);
            return "redirect:/employee/dashboard?completed";
        } catch (Exception e) {
            return "redirect:/employee/dashboard?error=" + e.getMessage();
        }
    }

    @PostMapping("/employee/jobs/{id}/delete")
    public String deleteJob(@PathVariable("id") Long id) {
        try {
            recordService.deleteRecord(id);
            return "redirect:/employee/dashboard?deleted";
        } catch (Exception e) {
            return "redirect:/employee/dashboard?error=" + e.getMessage();
        }
    }
}
