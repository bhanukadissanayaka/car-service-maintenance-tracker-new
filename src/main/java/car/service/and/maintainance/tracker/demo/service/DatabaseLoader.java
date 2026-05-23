package car.service.and.maintainance.tracker.demo.service;

import car.service.and.maintainance.tracker.demo.model.Car;
import car.service.and.maintainance.tracker.demo.model.Role;
import car.service.and.maintainance.tracker.demo.model.User;
import car.service.and.maintainance.tracker.demo.repository.CarRepository;
import car.service.and.maintainance.tracker.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@Component
@RequiredArgsConstructor
public class DatabaseLoader implements CommandLineRunner {

    private final UserService userService; // Uses registerUser to automatically BCrypt encrypt passwords
    private final UserRepository userRepository;
    private final CarRepository carRepository;

    @Override
    public void run(String... args) throws Exception {
        // 1. Seed Users from users.txt if database is empty
        if (userRepository.count() == 0) {
            ClassPathResource resource = new ClassPathResource("users.txt");
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;
                    String[] parts = line.split(",");
                    
                    User user = User.builder()
                            .username(parts[0].trim())
                            .password(parts[1].trim()) // Plaintext from file, gets encrypted below
                            .email(parts[2].trim())
                            .role(Role.valueOf(parts[3].trim()))
                            .fullName(parts[4].trim())
                            .phoneNumber(parts[5].trim())
                            .build();
                    
                    userService.registerUser(user); // Automatically BCrypt encodes the password
                }
            }
        }

        // 2. Seed Cars from cars.txt if database has no cars
        if (carRepository.count() == 0) {
            ClassPathResource resource = new ClassPathResource("cars.txt");
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;
                    String[] parts = line.split(",");
                    
                    String ownerUsername = parts[6].trim();
                    User owner = userRepository.findByUsername(ownerUsername).orElse(null);
                    
                    if (owner != null) {
                        Car car = Car.builder()
                                .make(parts[0].trim())
                                .model(parts[1].trim())
                                .year(Integer.parseInt(parts[2].trim()))
                                .licensePlate(parts[3].trim())
                                .vin(parts[4].trim())
                                .currentMileage(Integer.parseInt(parts[5].trim()))
                                .user(owner)
                                .build();
                        
                        carRepository.save(car);
                    }
                }
            }
        }
    }
}
