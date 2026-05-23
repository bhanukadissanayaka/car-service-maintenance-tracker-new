package car.service.and.maintainance.tracker.demo.model;

public enum Role {
    ROLE_USER,
    ROLE_EMPLOYEE,
    ROLE_ADMIN;

    public String getDisplayName() {
        switch (this) {
            case ROLE_USER: return "Car Owner";
            case ROLE_EMPLOYEE: return "Service Employee";
            case ROLE_ADMIN: return "System Administrator";
            default: return name();
        }
    }
}
