package nl.rug.ds.bpm.editor;

import nl.rug.ds.bpm.editor.core.enums.EventType;
import nl.rug.ds.bpm.editor.core.listeners.EventSource;

/**
 * Created by Mark on 8/1/2015.
 */
public class Console {
    public static void log(String log){
        EventSource.fireEvent(EventType.ADD_CONSOLE_LINE,log);
    }
    public static void error(String log){
        Console.log(log);
    }
    public static void warning(String log){
        Console.log(log);
    }
}
