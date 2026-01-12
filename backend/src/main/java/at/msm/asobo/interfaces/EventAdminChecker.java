package at.msm.asobo.interfaces;

import java.util.UUID;

public interface EventAdminChecker {
    boolean isUserAdminOfEvent(UUID eventId, UUID userId);
}
