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

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class CarController {

    private final CarService carService;
    private final MaintenanceRecordService recordService;
    private final MaintenanceReminderService reminderService;

    @GetMapping("/cars/add")
    public String addCarForm(Model model) {
        model.addAttribute("car", new Car());
        return "add-car";
    }

    @PostMapping("/cars/add")
    public String addCar(@ModelAttribute("car") Car car, Principal principal) {
        String username = principal.getName();
        carService.saveCar(car, username);
        return "redirect:/dashboard?carAdded";
    }

    @GetMapping("/cars/{id}")
    public String carDetails(@PathVariable("id") Long id, Model model) {
        Car car = carService.getCarById(id);
        List<MaintenanceRecord> records = recordService.getRecordsByCarId(id);
        List<MaintenanceReminder> reminders = reminderService.getRemindersByCarId(id);

        model.addAttribute("car", car);
        model.addAttribute("records", records);
        model.addAttribute("reminders", reminders);
        
        return "car-details";
    }

    @PostMapping("/cars/{id}/update-mileage")
    public String updateMileage(
            @PathVariable("id") Long id,
            @RequestParam("mileage") Integer mileage) {
        try {
            carService.updateMileage(id, mileage);
            return "redirect:/cars/" + id + "?mileageUpdated";
        } catch (IllegalArgumentException e) {
            return "redirect:/cars/" + id + "?error=" + e.getMessage();
        }
    }

    @PostMapping("/cars/{id}/delete")
    public String deleteCar(@PathVariable("id") Long id) {
        try {
            carService.deleteCar(id);
            return "redirect:/dashboard?carDeleted";
        } catch (IllegalArgumentException e) {
            return "redirect:/dashboard?error=" + e.getMessage();
        }
    }
}
