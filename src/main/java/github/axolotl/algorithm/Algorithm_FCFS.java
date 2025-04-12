package github.axolotl.algorithm;

import github.axolotl.elevator.Building;
import github.axolotl.elevator.Building.Elevator;
import github.axolotl.elevator.Direction;
import github.axolotl.passenger.Passenger;

import java.util.List;

/**
 * @author AxolotlXM
 * @version 1.0
 * @since 2025/4/12 21:30
 * <p>
 * 1.1 先来先服务算法（FCFS）
 * <p>
 * 先来先服务（FCFS-First Come First Serve）算法，是一种随即服务算法，它不仅仅没有对寻找楼层进行优化，也没有实时性的特征，它是一种最简单的电梯调度算法。
 * 它根据乘客请求乘坐电梯的先后次序进行调度。此算法的优点是公平、简单，且每个乘客的请求都能依次地得到处理，不会出现某一乘客的请求长期得不到满足的情况。这种方法在载荷较轻松的环境下，性能尚可接受，但是在载荷较大的情况下，这种算法的性能就会严重下降，甚至恶化。
 * 人们之所以研究这种在载荷较大的情况下几乎不可用的算法，有两个原因：
 * <p>
 * 任何调度算法在请求队列长度为1时，请求速率极低或相邻请求的间隔为无穷大时使用先来先服务算法既对调度效率不会产生影响，而且实现这种算法极其简单。
 * 先来先服务算法可以作为衡量其他算法的标准。
 */
public class Algorithm_FCFS implements ElevatorSchedulingAlgorithm {


    @Override
    public Elevator doLogic(Building building, Elevator elevator) {
        if (!elevator.getPassengers().isEmpty()) {
            //有乘客了
            Passenger passenger = elevator.getPassengers().get(0);
            if (elevator.getCurrentFloor() <= passenger.getTargetFloorHeight(building)) {
                elevator.setDirection(Direction.UP);
            } else
                elevator.setDirection(Direction.DOWN);

            return elevator;
        }


        List<Passenger> waitingPassengers = building.getWaitingPassengers();
//        System.out.println("waitingPassengers = " + waitingPassengers);
        if (!waitingPassengers.isEmpty()) {
            Passenger passenger = waitingPassengers.get(0);
            if (elevator.getCurrentFloor() <= passenger.getOriginFloorHeight(building)) {
                elevator.setDirection(Direction.UP);
            } else
                elevator.setDirection(Direction.DOWN);
        }
        return elevator;
    }

    @Override
    public Elevator passengerCall(Building building, Elevator elevator, Passenger passenger) {
        return null;
    }
}
