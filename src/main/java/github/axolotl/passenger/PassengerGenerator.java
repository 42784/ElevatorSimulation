package github.axolotl.passenger;

import github.axolotl.elevator.Building;
import github.axolotl.passenger.rule.PassengerGenerationRule;

import java.util.ArrayList;
import java.util.List;

/**
 * @author AxolotlXM
 * @version 1.0
 * @since 2025/4/13 1:52
 */
// 主生成器类
public class PassengerGenerator {
    private final List<PassengerGenerationRule> rules = new ArrayList<>();

    public void addRule(PassengerGenerationRule rule) {
        rules.add(rule);
    }

    public void tick(Building building) {
        for (PassengerGenerationRule rule : rules) {
            if (rule.shouldGenerate(building.getCurrentTime())) {
                Passenger passenger = rule.generatePassenger(building);
                building.passengerCall(passenger);
            }
        }
    }
}
