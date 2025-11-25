package at.msm.asobo.dto.medium;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MediumWithEventTitleDTO {

    @JsonProperty("medium")
    private MediumDTO mediumDTO;

    private String eventTitle;

    public MediumWithEventTitleDTO() {
    }

    public MediumDTO getMediumDTO() {
        return this.mediumDTO;
    }

    public void setMediumDTO(MediumDTO mediumDTO) {
        this.mediumDTO = mediumDTO;
    }

    public String getEventTitle() {
        return this.eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }
}
