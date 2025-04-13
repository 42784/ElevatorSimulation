package github.axolotl.algorithm;

import github.axolotl.elevator.Building;
import github.axolotl.elevator.Building.Elevator;
import github.axolotl.elevator.Direction;
import github.axolotl.passenger.Passenger;

import java.util.List;

/**
 * 扫描算法(SCAN)
 * 电梯会沿一个方向移动，直到该方向没有请求为止，然后改变方向。
 * 与LOOK不同，SCAN会扫描到建筑物的最顶层和最底层，然后再改变方向。
 * 
 * @author AxolotlXM
 * @version 1.0
 * @since 2025/4/16
 */
public class Algorithm_SCAN implements ElevatorSchedulingAlgorithm {

    @Override
    public Elevator doLogic(Building building, Elevator elevator) {
        // 如果电梯处于等待状态，不做调整
        if (elevator.getDirection() == Direction.WAIT) {
            return elevator;
        }
        
        double currentFloor = elevator.getCurrentFloor();
        int totalFloors = building.getFloors();
        double maxFloorHeight = (totalFloors - 1) * building.getFloorHeight();
        
        // 检查是否到达建筑物顶层或底层
        if (currentFloor >= maxFloorHeight && elevator.getDirection() == Direction.UP) {
            elevator.setDirection(Direction.DOWN);
            return elevator;
        } else if (currentFloor <= 0 && elevator.getDirection() == Direction.DOWN) {
            elevator.setDirection(Direction.UP);
            return elevator;
        }
        
        // 如果电梯内有乘客，按照SCAN算法处理
        if (!elevator.getPassengers().isEmpty()) {
            return elevator; // 保持当前方向，直到扫描到顶层或底层
        }
        
        // 检查当前方向是否还有请求
        List<Passenger> waitingPassengers = building.getWaitingPassengers();
        if (waitingPassengers.isEmpty()) {
            // 如果没有等待的乘客，继续扫描到端点
            return elevator;
        }
        
        // 检查当前方向是否还有请求
        boolean hasRequestsInCurrentDirection = false;
        if (elevator.getDirection() == Direction.UP) {
            for (Passenger passenger : waitingPassengers) {
                double originFloor = passenger.getOriginFloorHeight(building);
                if (originFloor > currentFloor) {
                    hasRequestsInCurrentDirection = true;
                    break;
                }
            }
        } else if (elevator.getDirection() == Direction.DOWN) {
            for (Passenger passenger : waitingPassengers) {
                double originFloor = passenger.getOriginFloorHeight(building);
                if (originFloor < currentFloor) {
                    hasRequestsInCurrentDirection = true;
                    break;
                }
            }
        }
        
        // 如果当前方向没有请求，且电梯未到达端点，继续按当前方向移动
        if (!hasRequestsInCurrentDirection) {
            // 继续扫描，不改变方向
            return elevator;
        }
        
        return elevator;
    }

    @Override
    public Elevator passengerCall(Building building, Elevator elevator, Passenger passenger) {
        // 在SCAN算法中，只在到达端点时改变方向，无需在此处处理
        return elevator;
    }
} 