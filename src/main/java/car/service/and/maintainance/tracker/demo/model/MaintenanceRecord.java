package car.service.and.maintainance.tracker.demo.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "maintenance_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"car", "assignedEmployee"})
@EqualsAndHashCode(exclude = {"car", "assignedEmployee"})
public class MaintenanceRecord {

    public enum MaintenanceStatus {
        SCHEDULED,
        IN_PROGRESS,
        COMPLETED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "service_type", nullable = false)
    private String serviceType;

    @Column(name = "service_date", nullable = false)
    private LocalDate serviceDate;

    @Column(name = "service_mileage", nullable = false)
    private Integer serviceMileage;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal cost;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private MaintenanceStatus status = MaintenanceStatus.SCHEDULED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id", nullable = false)
    private Car car;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = true)
    private User assignedEmployee;
}
