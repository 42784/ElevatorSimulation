package github.axolotl.passenger.rule;

import github.axolotl.elevator.Building;
import github.axolotl.passenger.Passenger;

/**
 * @author AxolotlXM
 * @version 1.0
 * @since 2025/4/13 1:50
 */
public interface PassengerGenerationRule {
    boolean shouldGenerate(long currentTime);
    Passenger generatePassenger(Building building);
}
