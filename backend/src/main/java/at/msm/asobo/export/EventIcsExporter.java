package at.msm.asobo.export;

import at.msm.asobo.entities.Event;
import java.nio.charset.StandardCharsets;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.Uid;
import org.springframework.stereotype.Component;

@Component
public class EventIcsExporter {

  private static final String PROD_ID = "-//asobō//EN";

  public byte[] buildIcs(Event event) {
    VEvent vEvent = new VEvent(event.getDate(), event.getTitle());
    vEvent.add(new Uid(event.getId().toString()));

    if (event.getDescription() != null) {
      vEvent.add(new Description(event.getDescription()));
    }
    if (event.getLocation() != null) {
      vEvent.add(new Location(event.getLocation()));
    }

    Calendar calendar =
        new Calendar().withProdId(PROD_ID).withDefaults().withComponent(vEvent).getFluentTarget();

    return calendar.toString().getBytes(StandardCharsets.UTF_8);
  }
}
