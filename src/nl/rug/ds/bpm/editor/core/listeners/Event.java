package nl.rug.ds.bpm.editor.core.listeners;

import nl.rug.ds.bpm.editor.core.enums.EventType;

/**
 * Created by Mark Kloosterhuis.
 */
public class Event{
    public EventType eventType;
    public EventListener listener;

    public Event(EventType eventType,EventListener listener){
        this.eventType = eventType;
        this.listener = listener;
    }
}