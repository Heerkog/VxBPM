package nl.rug.ds.bpm.editor.core.listeners;

import nl.rug.ds.bpm.editor.core.enums.EventType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mark Kloosterhuis.
 */
public class EventSource {
    public static boolean Enabled = true;
    public static List<Event> eventListeners = null;

    static {
        eventListeners = new ArrayList<>();


    }

    public static void addListener(EventType eventType, EventListener listener) {
        Event event = new Event(eventType, listener);
        eventListeners.add(event);
    }

    public static void fireEvent(EventType eventType, Object object) {
        if (Enabled) {
            try {
                System.out.println(eventType.toString());
                eventListeners.stream().filter(e -> e.eventType == eventType).forEach(e -> {
                    e.listener.invoke(object);
                });
            } catch (Exception e) {
                //e.printStackTrace();
            }
        }
    }

}


