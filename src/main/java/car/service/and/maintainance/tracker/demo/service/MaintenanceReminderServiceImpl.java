package car.service.and.maintainance.tracker.demo.service;

import car.service.and.maintainance.tracker.demo.model.Car;
import car.service.and.maintainance.tracker.demo.model.MaintenanceReminder;
import car.service.and.maintainance.tracker.demo.model.MaintenanceReminder.ReminderStatus;
import car.service.and.maintainance.tracker.demo.model.User;
import car.service.and.maintainance.tracker.demo.repository.CarRepository;
import car.service.and.maintainance.tracker.demo.repository.MaintenanceReminderRepository;
import car.service.and.maintainance.tracker.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MaintenanceReminderServiceImpl implements MaintenanceReminderService {

    private final MaintenanceReminderRepository reminderRepository;
    private final CarRepository carRepository;
    private final UserRepository userRepository;

    @Override
    public MaintenanceReminder saveReminder(MaintenanceReminder reminder, Long carId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new IllegalArgumentException("Car not found with ID: " + carId));
        
        reminder.setCar(car);
        reminder.setStatus(ReminderStatus.PENDING);
        
        // Intelligent Odometer and Date check
        LocalDate now = LocalDate.now();
        boolean isOverdue = false;
        
        if (reminder.getDueMileage() != null && car.getCurrentMileage() >= (reminder.getDueMileage() - 500)) {
            isOverdue = true;
        }
        if (reminder.getDueDate() != null && reminder.getDueDate().isBefore(now.plusDays(7))) {
            isOverdue = true;
        }
        
        if (isOverdue) {
            reminder.setStatus(ReminderStatus.OVERDUE);
        }
        
        return reminderRepository.save(reminder);
    }

    @Override
    @Transactional(readOnly = true)
    public MaintenanceReminder getReminderById(Long id) {
        return reminderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reminder not found with ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MaintenanceReminder> getRemindersByCarId(Long carId) {
        return reminderRepository.findByCarId(carId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MaintenanceReminder> getActiveRemindersByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
                
        List<MaintenanceReminder> activeReminders = new ArrayList<>();
        activeReminders.addAll(reminderRepository.findByCarUserAndStatus(user, ReminderStatus.PENDING));
        activeReminders.addAll(reminderRepository.findByCarUserAndStatus(user, ReminderStatus.OVERDUE));
        
        return activeReminders;
    }

    @Override
    public void checkAndTriggerAlerts(Long carId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new IllegalArgumentException("Car not found with ID: " + carId));
                
        List<MaintenanceReminder> pending = reminderRepository.findByCarIdAndStatus(carId, ReminderStatus.PENDING);
        LocalDate now = LocalDate.now();
        
        for (MaintenanceReminder reminder : pending) {
            boolean overdue = false;
            if (reminder.getDueMileage() != null && car.getCurrentMileage() >= (reminder.getDueMileage() - 500)) {
                overdue = true;
            }
            if (reminder.getDueDate() != null && reminder.getDueDate().isBefore(now.plusDays(7))) {
                overdue = true;
            }
            
            if (overdue) {
                reminder.setStatus(ReminderStatus.OVERDUE);
                reminderRepository.save(reminder);
            }
        }
    }
}
