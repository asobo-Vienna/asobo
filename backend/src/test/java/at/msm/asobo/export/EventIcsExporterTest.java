package at.msm.asobo.export;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import at.msm.asobo.builders.EventTestBuilder;
import at.msm.asobo.entities.Event;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.component.VEvent;
import org.junit.jupiter.api.Test;

class EventIcsExporterTest {

  private final EventIcsExporter exporter = new EventIcsExporter();

  private Calendar parse(byte[] data) throws Exception {
    return new CalendarBuilder().build(new ByteArrayInputStream(data));
  }

  private VEvent firstVEvent(Calendar calendar) {
    return (VEvent)
        calendar.getComponents().stream()
            .filter(c -> Component.VEVENT.equals(c.getName()))
            .findFirst()
            .orElseThrow();
  }

  @Test
  void buildIcs_setsSummaryFromTitle() throws Exception {
    Event event = new EventTestBuilder().withTitle("Birthday").buildEventEntity();

    VEvent vEvent = firstVEvent(parse(exporter.buildIcs(event)));

    assertEquals("Birthday", vEvent.getProperty("SUMMARY").orElseThrow().getValue());
  }

  @Test
  void buildIcs_setsUidFromEventId() throws Exception {
    Event event = new EventTestBuilder().buildEventEntity();

    VEvent vEvent = firstVEvent(parse(exporter.buildIcs(event)));

    assertEquals(event.getId().toString(), vEvent.getProperty("UID").orElseThrow().getValue());
  }

  @Test
  void buildIcs_setsDtStart() throws Exception {
    Event event = new EventTestBuilder().buildEventEntity();

    VEvent vEvent = firstVEvent(parse(exporter.buildIcs(event)));

    assertTrue(vEvent.getProperty("DTSTART").isPresent());
  }

  @Test
  void buildIcs_includesDescriptionWhenSet() throws Exception {
    Event event = new EventTestBuilder().withDescription("My party").buildEventEntity();

    VEvent vEvent = firstVEvent(parse(exporter.buildIcs(event)));

    assertEquals("My party", vEvent.getProperty("DESCRIPTION").orElseThrow().getValue());
  }

  @Test
  void buildIcs_omitsDescriptionWhenNull() throws Exception {
    Event event = new EventTestBuilder().withDescription(null).buildEventEntity();

    VEvent vEvent = firstVEvent(parse(exporter.buildIcs(event)));

    assertTrue(vEvent.getProperty("DESCRIPTION").isEmpty());
  }

  @Test
  void buildIcs_includesLocationWhenSet() throws Exception {
    Event event = new EventTestBuilder().withLocation("Vienna").buildEventEntity();

    VEvent vEvent = firstVEvent(parse(exporter.buildIcs(event)));

    assertEquals("Vienna", vEvent.getProperty("LOCATION").orElseThrow().getValue());
  }

  @Test
  void buildIcs_omitsLocationWhenNull() throws Exception {
    Event event = new EventTestBuilder().withLocation(null).buildEventEntity();

    VEvent vEvent = firstVEvent(parse(exporter.buildIcs(event)));

    assertTrue(vEvent.getProperty("LOCATION").isEmpty());
  }

  @Test
  void buildIcs_setsProdId() throws Exception {
    Event event = new EventTestBuilder().buildEventEntity();

    Calendar calendar = parse(exporter.buildIcs(event));

    assertEquals("-//asobō//EN", calendar.getProperty("PRODID").orElseThrow().getValue());
  }

  @Test
  void buildIcs_outputIsUtf8Encoded() {
    Event event = new EventTestBuilder().buildEventEntity();

    String body = new String(exporter.buildIcs(event), StandardCharsets.UTF_8);

    assertTrue(
        body.contains("asobō"), "PRODID should preserve non-ASCII characters in UTF-8 bytes");
  }

  @Test
  void buildIcs_outputParsesAsValidCalendar() {
    Event event = new EventTestBuilder().buildEventEntity();

    assertDoesNotThrow(() -> parse(exporter.buildIcs(event)));
  }
}
