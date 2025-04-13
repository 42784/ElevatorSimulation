package github.axolotl.algorithm;

import github.axolotl.elevator.Building;
import github.axolotl.elevator.Building.Elevator;
import github.axolotl.elevator.Direction;
import github.axolotl.passenger.Passenger;

import java.util.ArrayList;
import java.util.List;

/**
 * LOOK电梯调度算法
 * LOOK算法是一种优化的扫描算法，电梯在运行过程中，会一直沿着当前方向运行，直到该方向没有乘客请求为止，
 * 然后改变运行方向。不同于SCAN算法，LOOK不会强制电梯运行到端点。
 * 
 * @author AxolotlXM
 * @version 1.0
 * @since 2025/4/15
 */
public class Algorithm_LOOK implements ElevatorSchedulingAlgorithm {

    @Override
    public Elevator doLogic(Building building, Elevator elevator) {
        if (elevator.getDirection() == Direction.WAIT) {
            return elevator;
        }
        
        // 如果电梯中有乘客，优先考虑电梯内的请求
        if (!elevator.getPassengers().isEmpty()) {
            handlePassengersInElevator(building, elevator);
            return elevator;
        }
        
        // 如果电梯中没有乘客，处理等待的乘客请求
        List<Passenger> waitingPassengers = building.getWaitingPassengers();
        if (!waitingPassengers.isEmpty()) {
            handleWaitingPassengers(building, elevator);
        } else {
            // 如果没有等待的乘客，电梯进入空闲状态
            elevator.setDirection(Direction.IDLE);
        }
        
        return elevator;
    }

    /**
     * 处理电梯内的乘客请求
     */
    private void handlePassengersInElevator(Building building, Elevator elevator) {
        double currentFloor = elevator.getCurrentFloor();
        boolean hasRequestsAbove = false;
        boolean hasRequestsBelow = false;
        
        // 检查是否有向上或向下的请求
        for (Passenger passenger : elevator.getPassengers()) {
            double targetFloor = passenger.getTargetFloorHeight(building);
            if (targetFloor > currentFloor) {
                hasRequestsAbove = true;
            } else if (targetFloor < currentFloor) {
                hasRequestsBelow = true;
            }
        }
        
        // 根据LOOK算法调整电梯方向
        if (elevator.getDirection() == Direction.UP) {
            if (hasRequestsAbove) {
                elevator.setDirection(Direction.UP);
            } else if (hasRequestsBelow) {
                elevator.setDirection(Direction.DOWN);
            } else {
                elevator.setDirection(Direction.IDLE);
            }
        } else if (elevator.getDirection() == Direction.DOWN) {
            if (hasRequestsBelow) {
                elevator.setDirection(Direction.DOWN);
            } else if (hasRequestsAbove) {
                elevator.setDirection(Direction.UP);
            } else {
                elevator.setDirection(Direction.IDLE);
            }
        } else {
            // 如果是IDLE状态，根据最近的请求决定方向
            if (hasRequestsAbove) {
                elevator.setDirection(Direction.UP);
            } else if (hasRequestsBelow) {
                elevator.setDirection(Direction.DOWN);
            }
        }
    }

    /**
     * 处理等待的乘客请求
     */
    private void handleWaitingPassengers(Building building, Elevator elevator) {
        double currentFloor = elevator.getCurrentFloor();
        boolean hasRequestsAbove = false;
        boolean hasRequestsBelow = false;
        
        // 检查是否有向上或向下的请求
        for (Passenger passenger : building.getWaitingPassengers()) {
            double originFloor = passenger.getOriginFloorHeight(building);
            if (originFloor > currentFloor) {
                hasRequestsAbove = true;
            } else if (originFloor < currentFloor) {
                hasRequestsBelow = true;
            }
        }
        
        // 根据LOOK算法调整电梯方向
        if (elevator.getDirection() == Direction.UP) {
            if (hasRequestsAbove) {
                elevator.setDirection(Direction.UP);
            } else if (hasRequestsBelow) {
                elevator.setDirection(Direction.DOWN);
            } else {
                elevator.setDirection(Direction.IDLE);
            }
        } else if (elevator.getDirection() == Direction.DOWN) {
            if (hasRequestsBelow) {
                elevator.setDirection(Direction.DOWN);
            } else if (hasRequestsAbove) {
                elevator.setDirection(Direction.UP);
            } else {
                elevator.setDirection(Direction.IDLE);
            }
        } else {
            // 如果是IDLE状态，根据最近的请求决定方向
            Passenger nearestPassenger = findNearestPassenger(building, elevator);
            if (nearestPassenger != null) {
                double originFloor = nearestPassenger.getOriginFloorHeight(building);
                if (originFloor > currentFloor) {
                    elevator.setDirection(Direction.UP);
                } else if (originFloor < currentFloor) {
                    elevator.setDirection(Direction.DOWN);
                }
            }
        }
    }

    /**
     * 查找最近的乘客
     */
    private Passenger findNearestPassenger(Building building, Elevator elevator) {
        double currentFloor = elevator.getCurrentFloor();
        double minDistance = Double.MAX_VALUE;
        Passenger nearestPassenger = null;
        
        for (Passenger passenger : building.getWaitingPassengers()) {
            double distance = Math.abs(passenger.getOriginFloorHeight(building) - currentFloor);
            if (distance < minDistance) {
                minDistance = distance;
                nearestPassenger = passenger;
            }
        }
        
        return nearestPassenger;
    }

    @Override
    public Elevator passengerCall(Building building, Elevator elevator, Passenger passenger) {
        // 在接收到乘客呼叫时可以优化电梯行为，但基本逻辑在doLogic中已处理
        return elevator;
    }
} 