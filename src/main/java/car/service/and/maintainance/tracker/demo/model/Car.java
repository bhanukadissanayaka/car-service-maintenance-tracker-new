package car.service.and.maintainance.tracker.demo.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cars")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"user", "maintenanceRecords", "maintenanceReminders"})
@EqualsAndHashCode(exclude = {"user", "maintenanceRecords", "maintenanceReminders"})
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String make;

    @Column(nullable = false)
    private String model;

    @Column(name = "manufacture_year", nullable = false)
    private Integer year;

    @Column(name = "license_plate", nullable = false)
    private String licensePlate;

    @Column(unique = true)
    private String vin;

    @Column(name = "current_mileage", nullable = false)
    private Integer currentMileage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<MaintenanceRecord> maintenanceRecords = new ArrayList<>();

    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<MaintenanceReminder> maintenanceReminders = new ArrayList<>();
}
