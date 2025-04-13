package github.axolotl.algorithm;

import github.axolotl.elevator.Building;
import github.axolotl.elevator.Building.Elevator;
import github.axolotl.elevator.Direction;
import github.axolotl.passenger.Passenger;

import java.util.Comparator;
import java.util.List;

/**
 * 最短寻找时间优先算法（SSTF）
 * 该算法优先服务距离当前电梯位置最近的乘客请求，
 * 类似于磁盘调度中的最短寻道时间优先算法
 * 
 * @author AxolotlXM
 * @version 1.0
 * @since 2025/4/16
 */
public class Algorithm_SSTF implements ElevatorSchedulingAlgorithm {

    @Override
    public Elevator doLogic(Building building, Elevator elevator) {
        // 如果电梯处于等待状态，不做调整
        if (elevator.getDirection() == Direction.WAIT) {
            return elevator;
        }
        
        // 如果电梯内有乘客，优先处理电梯内乘客的请求
        if (!elevator.getPassengers().isEmpty()) {
            handlePassengersInElevator(building, elevator);
            return elevator;
        }
        
        // 如果电梯内没有乘客，查找最近的外部请求
        List<Passenger> waitingPassengers = building.getWaitingPassengers();
        if (!waitingPassengers.isEmpty()) {
            // 找到距离电梯当前位置最近的乘客
            Passenger nearestPassenger = findNearestPassenger(building, elevator, waitingPassengers);
            
            // 根据最近乘客的位置设置电梯方向
            double currentFloor = elevator.getCurrentFloor();
            double passengerFloor = nearestPassenger.getOriginFloorHeight(building);
            
            if (Math.abs(currentFloor - passengerFloor) < 0.01) {
                // 电梯已经到达乘客所在楼层，根据乘客目标楼层设置方向
                double targetFloor = nearestPassenger.getTargetFloorHeight(building);
                if (targetFloor > currentFloor) {
                    elevator.setDirection(Direction.UP);
                } else {
                    elevator.setDirection(Direction.DOWN);
                }
            } else if (passengerFloor > currentFloor) {
                elevator.setDirection(Direction.UP);
            } else {
                elevator.setDirection(Direction.DOWN);
            }
        } else {
            // 没有等待的乘客，设置为空闲状态
            elevator.setDirection(Direction.IDLE);
        }
        
        return elevator;
    }
    
    /**
     * 处理电梯内乘客的请求
     */
    private void handlePassengersInElevator(Building building, Elevator elevator) {
        double currentFloor = elevator.getCurrentFloor();
        Passenger nearestPassenger = findNearestPassengerInElevator(building, elevator);
        
        if (nearestPassenger != null) {
            double targetFloor = nearestPassenger.getTargetFloorHeight(building);
            
            if (targetFloor > currentFloor) {
                elevator.setDirection(Direction.UP);
            } else if (targetFloor < currentFloor) {
                elevator.setDirection(Direction.DOWN);
            } else {
                elevator.setDirection(Direction.IDLE);
            }
        }
    }
    
    /**
     * 找到电梯内距离当前位置最近的乘客
     */
    private Passenger findNearestPassengerInElevator(Building building, Elevator elevator) {
        double currentFloor = elevator.getCurrentFloor();
        return elevator.getPassengers().stream()
                .min(Comparator.comparingDouble(passenger -> 
                        Math.abs(passenger.getTargetFloorHeight(building) - currentFloor)))
                .orElse(null);
    }
    
    /**
     * 找到等待乘客中距离电梯当前位置最近的乘客
     */
    private Passenger findNearestPassenger(Building building, Elevator elevator, List<Passenger> waitingPassengers) {
        double currentFloor = elevator.getCurrentFloor();
        return waitingPassengers.stream()
                .min(Comparator.comparingDouble(passenger -> 
                        Math.abs(passenger.getOriginFloorHeight(building) - currentFloor)))
                .orElse(null);
    }

    @Override
    public Elevator passengerCall(Building building, Elevator elevator, Passenger passenger) {
        // 这里不需要特殊处理，因为在doLogic中已经实现了SSTF逻辑
        return elevator;
    }
} 