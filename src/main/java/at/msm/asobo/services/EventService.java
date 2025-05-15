package at.msm.asobo.services;

import at.msm.asobo.entities.Event;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class EventService {
    private ArrayList<Event> events;

    public EventService(){
        this.events= new ArrayList<Event>();
    }

    public ArrayList<Event> getAllEvents(){
        return events;
    }
}
