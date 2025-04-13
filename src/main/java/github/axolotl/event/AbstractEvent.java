package github.axolotl.event;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author AxolotlXM
 * @Created by Axolotl
 * @Date 2025/4/13 13:52
 */
@Data
@AllArgsConstructor
public abstract class AbstractEvent {
    private EventType type;
}
