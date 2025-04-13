package github.axolotl.event;

import github.axolotl.elevator.Building;
import github.axolotl.passenger.Passenger;
import lombok.Getter;

/**
 * @author AxolotlXM
 * @Created by Axolotl
 * @Date 2025/4/13 13:56
 */
@Getter
public class ElevatorEvent extends AbstractEvent {
    private final Building building;
    private final Building.Elevator elevator;
    private Passenger passenger;

    public ElevatorEvent(EventType type, Building building, Building.Elevator elevator) {
        super(type);
        this.building = building;
        this.elevator = elevator;
    }

    public ElevatorEvent(EventType type, Building building, Building.Elevator elevator, Passenger passenger) {
        super(type);
        this.building = building;
        this.elevator = elevator;
        this.passenger = passenger;
    }
}
