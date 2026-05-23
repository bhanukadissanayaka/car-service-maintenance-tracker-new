package car.service.and.maintainance.tracker.demo.service;

import car.service.and.maintainance.tracker.demo.model.Car;
import car.service.and.maintainance.tracker.demo.model.MaintenanceRecord;
import car.service.and.maintainance.tracker.demo.model.MaintenanceRecord.MaintenanceStatus;
import car.service.and.maintainance.tracker.demo.model.MaintenanceReminder;
import car.service.and.maintainance.tracker.demo.model.User;
import car.service.and.maintainance.tracker.demo.repository.CarRepository;
import car.service.and.maintainance.tracker.demo.repository.MaintenanceRecordRepository;
import car.service.and.maintainance.tracker.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MaintenanceRecordServiceImpl implements MaintenanceRecordService {

    private final MaintenanceRecordRepository recordRepository;
    private final CarRepository carRepository;
    private final UserRepository userRepository;

    @Override
    public MaintenanceRecord scheduleService(MaintenanceRecord record, Long carId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new IllegalArgumentException("Car not found with ID: " + carId));
        
        record.setCar(car);
        record.setStatus(MaintenanceStatus.SCHEDULED);
        if (record.getServiceDate() == null) {
            record.setServiceDate(LocalDate.now());
        }
        if (record.getServiceMileage() == null) {
            record.setServiceMileage(car.getCurrentMileage());
        }
        if (record.getCost() == null) {
            record.setCost(BigDecimal.ZERO);
        }
        
        return recordRepository.save(record);
    }

    @Override
    @Transactional(readOnly = true)
    public MaintenanceRecord getRecordById(Long id) {
        return recordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Maintenance Record not found with ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MaintenanceRecord> getRecordsByCarId(Long carId) {
        return recordRepository.findByCarIdOrderByServiceDateDesc(carId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MaintenanceRecord> getRecordsByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return recordRepository.findByCarUserOrderByServiceDateDesc(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MaintenanceRecord> getUnassignedRecords() {
        return recordRepository.findByStatus(MaintenanceStatus.SCHEDULED);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MaintenanceRecord> getRecordsByEmployeeUsername(String employeeUsername) {
        User employee = userRepository.findByUsername(employeeUsername)
                .orElseThrow(() -> new UsernameNotFoundException("Employee not found: " + employeeUsername));
        return recordRepository.findByAssignedEmployee(employee);
    }

    @Override
    public void claimJob(Long recordId, String employeeUsername) {
        MaintenanceRecord record = recordRepository.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("Record not found with ID: " + recordId));
        
        if (record.getStatus() != MaintenanceStatus.SCHEDULED) {
            throw new IllegalStateException("Job is already claimed or completed");
        }
        
        User employee = userRepository.findByUsername(employeeUsername)
                .orElseThrow(() -> new UsernameNotFoundException("Employee not found: " + employeeUsername));
        
        record.setAssignedEmployee(employee);
        record.setStatus(MaintenanceStatus.IN_PROGRESS);
        recordRepository.save(record);
    }

    @Override
    public void completeJob(Long recordId, String notes, BigDecimal cost, Integer serviceMileage) {
        MaintenanceRecord record = recordRepository.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("Record not found with ID: " + recordId));
        
        if (record.getStatus() != MaintenanceStatus.IN_PROGRESS) {
            throw new IllegalStateException("Job must be In Progress to complete");
        }
        
        record.setNotes(notes);
        record.setCost(cost);
        record.setServiceMileage(serviceMileage);
        record.setServiceDate(LocalDate.now());
        record.setStatus(MaintenanceStatus.COMPLETED);
        
        // Update car odometer if higher
        Car car = record.getCar();
        if (serviceMileage > car.getCurrentMileage()) {
            car.setCurrentMileage(serviceMileage);
        }
        
        // Resolve reminders targeting this type of service
        if (car.getMaintenanceReminders() != null) {
            for (MaintenanceReminder reminder : car.getMaintenanceReminders()) {
                if (reminder.getStatus() != MaintenanceReminder.ReminderStatus.COMPLETED) {
                    // Check if reminder description contains service type keyword case-insensitively
                    String serviceWord = record.getServiceType().toLowerCase();
                    String reminderWord = reminder.getDescription().toLowerCase();
                    if (reminderWord.contains(serviceWord) || serviceWord.contains(reminderWord)) {
                        reminder.setStatus(MaintenanceReminder.ReminderStatus.COMPLETED);
                    }
                }
            }
        }
        
        carRepository.save(car);
        recordRepository.save(record);
    }

    @Override
    public void deleteRecord(Long id) {
        if (!recordRepository.existsById(id)) {
            throw new IllegalArgumentException("Maintenance Record not found with ID: " + id);
        }
        recordRepository.deleteById(id);
    }
}
