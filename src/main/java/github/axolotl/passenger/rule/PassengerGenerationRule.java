package github.axolotl.passenger.rule;

import github.axolotl.elevator.Building;
import github.axolotl.event.EventRecoder;
import github.axolotl.event.EventType;
import github.axolotl.event.PassengerEvent;
import github.axolotl.passenger.Passenger;

/**
 * @author AxolotlXM
 * @version 1.0
 * @since 2025/4/13 1:50
 */
public interface PassengerGenerationRule {
    boolean shouldGenerate(long currentTime);
    Passenger generate(Building building);

}
