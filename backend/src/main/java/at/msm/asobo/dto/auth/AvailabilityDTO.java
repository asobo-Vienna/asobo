package at.msm.asobo.dto.auth;

public class AvailabilityDTO {
    private boolean available;

    public AvailabilityDTO(boolean available) {
        this.available = available;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
