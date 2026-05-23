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
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class MaintenanceController {

    private final CarService carService;
    private final MaintenanceRecordService recordService;
    private final MaintenanceReminderService reminderService;

    @GetMapping("/cars/{carId}/records/add")
    public String addRecordForm(@PathVariable("carId") Long carId, Model model) {
        Car car = carService.getCarById(carId);
        model.addAttribute("car", car);
        model.addAttribute("record", new MaintenanceRecord());
        return "add-record";
    }

    @PostMapping("/cars/{carId}/records/add")
    public String addRecord(
            @PathVariable("carId") Long carId,
            @ModelAttribute("record") MaintenanceRecord record) {
        recordService.scheduleService(record, carId);
        return "redirect:/cars/" + carId + "?serviceScheduled";
    }

    @GetMapping("/cars/{carId}/reminders/add")
    public String addReminderForm(@PathVariable("carId") Long carId, Model model) {
        Car car = carService.getCarById(carId);
        model.addAttribute("car", car);
        model.addAttribute("reminder", new MaintenanceReminder());
        return "add-reminder";
    }

    @PostMapping("/cars/{carId}/reminders/add")
    public String addReminder(
            @PathVariable("carId") Long carId,
            @ModelAttribute("reminder") MaintenanceReminder reminder) {
        reminderService.saveReminder(reminder, carId);
        return "redirect:/cars/" + carId + "?reminderCreated";
    }
}
