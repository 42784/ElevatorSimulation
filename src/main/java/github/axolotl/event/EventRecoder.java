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

    /**
     * 获取所有事件
     * @return 事件列表
     */
    public List<AbstractEvent> getEvents() {
        return events;
    }

    /**
     * 获取指定类型的事件
     * @param type 事件类型
     * @return 事件列表
     */
    public List<AbstractEvent> getEventsByType(EventType type) {
        return eventsByType.getOrDefault(type, new ArrayList<>());
    }

    /**
     * 获取事件统计信息
     * @return 事件类型和数量的映射
     */
    public Map<EventType, Integer> getEventStatistics() {
        Map<EventType, Integer> statistics = new HashMap<>();
        
        for (EventType type : eventsByType.keySet()) {
            statistics.put(type, eventsByType.get(type).size());
        }
        
        return statistics;
    }
}
