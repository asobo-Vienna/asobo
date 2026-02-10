package at.msm.asobo.dto.medium;

public class MediumWithEventTitleDTO extends MediumDTO {
    private String eventTitle;

    public MediumWithEventTitleDTO() {}

    public String getEventTitle() {
        return this.eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }
}
