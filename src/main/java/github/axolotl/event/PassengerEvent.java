package github.axolotl.event;

import github.axolotl.passenger.Passenger;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * @author AxolotlXM
 * @Created by Axolotl
 * @Date 2025/4/13 13:56
 */
public class PassengerEvent extends AbstractEvent {
    @Getter
    private final Passenger passenger;

    public PassengerEvent(EventType type, Passenger passenger) {
        super(type);
        this.passenger = passenger;
    }
}
