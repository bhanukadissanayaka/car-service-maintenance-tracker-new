package car.service.and.maintainance.tracker.demo.service;

import car.service.and.maintainance.tracker.demo.model.User;

import java.util.List;

public interface UserService {
    User registerUser(User user);
    User findByUsername(String username);
    User findByEmail(String email);
    List<User> getEmployees();
}
