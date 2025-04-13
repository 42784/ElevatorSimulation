package github.axolotl.event;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author AxolotlXM
 * @Created by Axolotl
 * @Date 2025/4/13 13:58
 */
@Data
public class EventRecoder {
    private List<AbstractEvent> events = new ArrayList<>();
    private Map<EventType, List<AbstractEvent>> eventsByType = new HashMap<>();

    public void addEvent(AbstractEvent event) {
        events.add(event);
        eventsByType.computeIfAbsent(event.getType(), k -> new ArrayList<>()).add(event);
    }


}
