package at.msm.asobo.interfaces;

import java.util.UUID;

public interface MediumWithEventTitle {
    UUID getId();
    UUID getEventId();
    String getMediumURI();
    String getEventTitle();
}
