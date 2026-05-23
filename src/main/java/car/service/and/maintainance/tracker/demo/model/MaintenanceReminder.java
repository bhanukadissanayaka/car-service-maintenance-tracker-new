package car.service.and.maintainance.tracker.demo.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "maintenance_reminders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"car"})
@EqualsAndHashCode(exclude = {"car"})
public class MaintenanceReminder {

    public enum ReminderStatus {
        PENDING,
        OVERDUE,
        COMPLETED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String description;

    @Column(name = "due_mileage")
    private Integer dueMileage;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ReminderStatus status = ReminderStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id", nullable = false)
    private Car car;
}
