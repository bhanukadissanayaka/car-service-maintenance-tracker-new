package car.service.and.maintainance.tracker.demo.service;

import car.service.and.maintainance.tracker.demo.model.Car;

import java.util.List;

public interface CarService {
    Car saveCar(Car car, String username);
    Car getCarById(Long id);
    List<Car> getCarsByUsername(String username);
    void updateMileage(Long carId, Integer mileage);
    void deleteCar(Long id);
}
