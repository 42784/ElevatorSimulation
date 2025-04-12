package github.axolotl.algorithm;

import github.axolotl.elevator.Building;
import github.axolotl.passenger.Passenger;

/**
 * @author AxolotlXM
 * @version 1.0
 * @since 2025/4/12 21:29
 */
public interface ElevatorSchedulingAlgorithm {
    Building.Elevator doLogic(Building building,Building.Elevator elevator );//执行一次算法逻辑
    Building.Elevator passengerCall(Building building,Building.Elevator elevator,  Passenger passenger);//乘客唤起电梯

}
