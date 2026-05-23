package car.service.and.maintainance.tracker.demo.service;

import car.service.and.maintainance.tracker.demo.model.Car;
import car.service.and.maintainance.tracker.demo.model.MaintenanceReminder;
import car.service.and.maintainance.tracker.demo.model.User;
import car.service.and.maintainance.tracker.demo.repository.CarRepository;
import car.service.and.maintainance.tracker.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;
    private final UserRepository userRepository;

    @Override
    public Car saveCar(Car car, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        car.setUser(user);

        // Handle optional unique VIN field by mapping empty strings to null
        if (car.getVin() != null && car.getVin().trim().isEmpty()) {
            car.setVin(null);
        }

        return carRepository.save(car);
    }

    @Override
    @Transactional(readOnly = true)
    public Car getCarById(Long id) {
        return carRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Car not found with ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Car> getCarsByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return carRepository.findByUser(user);
    }

    @Override
    public void updateMileage(Long carId, Integer mileage) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new IllegalArgumentException("Car not found with ID: " + carId));

        if (mileage < car.getCurrentMileage()) {
            throw new IllegalArgumentException("Odometer reading cannot be decreased from " + car.getCurrentMileage());
        }

        car.setCurrentMileage(mileage);

        // Intelligent Odometer Checker: update active pending reminders
        if (car.getMaintenanceReminders() != null) {
            for (MaintenanceReminder reminder : car.getMaintenanceReminders()) {
                if (reminder.getStatus() == MaintenanceReminder.ReminderStatus.PENDING) {
                    if (reminder.getDueMileage() != null && car.getCurrentMileage() >= (reminder.getDueMileage() - 500)) {
                        reminder.setStatus(MaintenanceReminder.ReminderStatus.OVERDUE);
                    }
                }
            }
        }

        carRepository.save(car);
    }

    @Override
    public void deleteCar(Long id) {
        if (!carRepository.existsById(id)) {
            throw new IllegalArgumentException("Car not found with ID: " + id);
        }
        carRepository.deleteById(id);
    }
}
