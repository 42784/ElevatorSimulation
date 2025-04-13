package github.axolotl.algorithm;

import github.axolotl.elevator.Building;
import github.axolotl.elevator.Building.Elevator;
import github.axolotl.elevator.Direction;
import github.axolotl.passenger.Passenger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * FD-SCAN电梯调度算法
 * FD-SCAN（Full Direction Scan）是一种综合考虑乘客等待方向和目标方向的算法。
 * 该算法在调度时，考虑乘客的当前方向和电梯的当前方向是否一致，优先服务方向一致的乘客，
 * 减少乘客的平均等待时间和电梯的能耗。
 * 
 * @author AxolotlXM
 * @version 1.0
 * @since 2025/4/15
 */
public class Algorithm_FDSCAN implements ElevatorSchedulingAlgorithm {

    // 存储每部电梯的请求队列
    private final Map<String, List<Passenger>> upRequestsMap = new HashMap<>();
    private final Map<String, List<Passenger>> downRequestsMap = new HashMap<>();

    @Override
    public Elevator doLogic(Building building, Elevator elevator) {
        String elevatorId = elevator.getId();
        
        // 确保请求队列存在
        if (!upRequestsMap.containsKey(elevatorId)) {
            upRequestsMap.put(elevatorId, new ArrayList<>());
        }
        if (!downRequestsMap.containsKey(elevatorId)) {
            downRequestsMap.put(elevatorId, new ArrayList<>());
        }
        
        List<Passenger> upRequests = upRequestsMap.get(elevatorId);
        List<Passenger> downRequests = downRequestsMap.get(elevatorId);
        
        // 处理电梯等待状态
        if (elevator.getDirection() == Direction.WAIT) {
            return elevator;
        }
        
        // 更新请求队列
        updateRequestQueues(building, elevator, upRequests, downRequests);
        
        // 判断电梯方向和处理乘客请求
        double currentFloor = elevator.getCurrentFloor();
        
        // 如果电梯内有乘客，优先考虑电梯内乘客
        if (!elevator.getPassengers().isEmpty()) {
            processInternalRequests(building, elevator);
        } else {
            // 电梯内没有乘客，处理外部请求
            processExternalRequests(elevator, upRequests, downRequests, currentFloor);
        }
        
        return elevator;
    }

    /**
     * 更新请求队列
     */
    private void updateRequestQueues(Building building, Elevator elevator, List<Passenger> upRequests, List<Passenger> downRequests) {
        // 清除已完成的请求
        upRequests.removeIf(p -> elevator.getPassengers().contains(p));
        downRequests.removeIf(p -> elevator.getPassengers().contains(p));
        
        // 添加新的外部请求
        for (Passenger passenger : building.getWaitingPassengers()) {
            if (passenger.getDirection() == Direction.UP && !upRequests.contains(passenger)) {
                upRequests.add(passenger);
            } else if (passenger.getDirection() == Direction.DOWN && !downRequests.contains(passenger)) {
                downRequests.add(passenger);
            }
        }
    }

    /**
     * 处理电梯内部乘客请求
     */
    private void processInternalRequests(Building building, Elevator elevator) {
        double currentFloor = elevator.getCurrentFloor();
        boolean hasUpRequests = false;
        boolean hasDownRequests = false;
        
        // 检查电梯内乘客的目标方向
        for (Passenger passenger : elevator.getPassengers()) {
            double targetFloor = passenger.getTargetFloorHeight(building);
            if (targetFloor > currentFloor) {
                hasUpRequests = true;
            } else if (targetFloor < currentFloor) {
                hasDownRequests = true;
            }
        }
        
        // 根据FD-SCAN逻辑调整电梯方向
        if (elevator.getDirection() == Direction.UP) {
            if (hasUpRequests) {
                elevator.setDirection(Direction.UP);
            } else if (hasDownRequests) {
                elevator.setDirection(Direction.DOWN);
            } else {
                elevator.setDirection(Direction.IDLE);
            }
        } else if (elevator.getDirection() == Direction.DOWN) {
            if (hasDownRequests) {
                elevator.setDirection(Direction.DOWN);
            } else if (hasUpRequests) {
                elevator.setDirection(Direction.UP);
            } else {
                elevator.setDirection(Direction.IDLE);
            }
        } else {
            // IDLE状态下，根据请求决定方向
            if (hasUpRequests) {
                elevator.setDirection(Direction.UP);
            } else if (hasDownRequests) {
                elevator.setDirection(Direction.DOWN);
            }
        }
    }

    /**
     * 处理外部请求
     */
    private void processExternalRequests(Elevator elevator, List<Passenger> upRequests, List<Passenger> downRequests, double currentFloor) {
        boolean hasUpRequests = !upRequests.isEmpty();
        boolean hasDownRequests = !downRequests.isEmpty();
        
        if (elevator.getDirection() == Direction.UP) {
            // 电梯向上运行
            if (hasUpRequestsAbove(upRequests, currentFloor)) {
                elevator.setDirection(Direction.UP);
            } else if (hasDownRequestsAbove(downRequests, currentFloor)) {
                elevator.setDirection(Direction.UP);
            } else if (hasDownRequestsBelow(downRequests, currentFloor)) {
                elevator.setDirection(Direction.DOWN);
            } else if (hasUpRequestsBelow(upRequests, currentFloor)) {
                elevator.setDirection(Direction.DOWN);
            } else {
                elevator.setDirection(Direction.IDLE);
            }
        } else if (elevator.getDirection() == Direction.DOWN) {
            // 电梯向下运行
            if (hasDownRequestsBelow(downRequests, currentFloor)) {
                elevator.setDirection(Direction.DOWN);
            } else if (hasUpRequestsBelow(upRequests, currentFloor)) {
                elevator.setDirection(Direction.DOWN);
            } else if (hasUpRequestsAbove(upRequests, currentFloor)) {
                elevator.setDirection(Direction.UP);
            } else if (hasDownRequestsAbove(downRequests, currentFloor)) {
                elevator.setDirection(Direction.UP);
            } else {
                elevator.setDirection(Direction.IDLE);
            }
        } else {
            // 电梯处于IDLE状态
            Passenger nearestPassenger = findNearestPassenger(upRequests, downRequests, currentFloor);
            if (nearestPassenger != null) {
                double passengerFloor = nearestPassenger.getOriginFloor();
                if (passengerFloor > currentFloor) {
                    elevator.setDirection(Direction.UP);
                } else if (passengerFloor < currentFloor) {
                    elevator.setDirection(Direction.DOWN);
                }
            }
        }
    }

    /**
     * 检查当前楼层以上是否有向上的请求
     */
    private boolean hasUpRequestsAbove(List<Passenger> upRequests, double currentFloor) {
        for (Passenger passenger : upRequests) {
            if (passenger.getOriginFloor() > currentFloor) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查当前楼层以下是否有向上的请求
     */
    private boolean hasUpRequestsBelow(List<Passenger> upRequests, double currentFloor) {
        for (Passenger passenger : upRequests) {
            if (passenger.getOriginFloor() < currentFloor) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查当前楼层以上是否有向下的请求
     */
    private boolean hasDownRequestsAbove(List<Passenger> downRequests, double currentFloor) {
        for (Passenger passenger : downRequests) {
            if (passenger.getOriginFloor() > currentFloor) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查当前楼层以下是否有向下的请求
     */
    private boolean hasDownRequestsBelow(List<Passenger> downRequests, double currentFloor) {
        for (Passenger passenger : downRequests) {
            if (passenger.getOriginFloor() < currentFloor) {
                return true;
            }
        }
        return false;
    }

    /**
     * 查找离电梯最近的乘客
     */
    private Passenger findNearestPassenger(List<Passenger> upRequests, List<Passenger> downRequests, double currentFloor) {
        Passenger nearestPassenger = null;
        double minDistance = Double.MAX_VALUE;
        
        // 检查向上请求
        for (Passenger passenger : upRequests) {
            double distance = Math.abs(passenger.getOriginFloor() - currentFloor);
            if (distance < minDistance) {
                minDistance = distance;
                nearestPassenger = passenger;
            }
        }
        
        // 检查向下请求
        for (Passenger passenger : downRequests) {
            double distance = Math.abs(passenger.getOriginFloor() - currentFloor);
            if (distance < minDistance) {
                minDistance = distance;
                nearestPassenger = passenger;
            }
        }
        
        return nearestPassenger;
    }

    @Override
    public Elevator passengerCall(Building building, Elevator elevator, Passenger passenger) {
        // 接收到乘客呼叫时更新请求队列
        String elevatorId = elevator.getId();
        
        if (!upRequestsMap.containsKey(elevatorId)) {
            upRequestsMap.put(elevatorId, new ArrayList<>());
        }
        if (!downRequestsMap.containsKey(elevatorId)) {
            downRequestsMap.put(elevatorId, new ArrayList<>());
        }
        
        if (passenger.getDirection() == Direction.UP && !upRequestsMap.get(elevatorId).contains(passenger)) {
            upRequestsMap.get(elevatorId).add(passenger);
        } else if (passenger.getDirection() == Direction.DOWN && !downRequestsMap.get(elevatorId).contains(passenger)) {
            downRequestsMap.get(elevatorId).add(passenger);
        }
        
        return elevator;
    }
} 