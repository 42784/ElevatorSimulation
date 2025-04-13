package github.axolotl.passenger;

import github.axolotl.elevator.Building;
import github.axolotl.elevator.Direction;
import lombok.Data;

/**
 * @author AxolotlXM
 * @version 1.0
 * @since 2025/4/12 20:41
 */
@Data
public class Passenger {
    private final int originFloor;
    private final int targetFloor;
    private Direction direction;//借用这个的UP共和DOWN表示乘客的按下
    private long waitingTime = 0;//总共等待时长
    private long arriveTime = 0;//乘坐电梯时间

    public Passenger(int originFloor, int targetFloor) {
        this.originFloor = originFloor;
        this.targetFloor = targetFloor;

        if (originFloor <= targetFloor) direction = Direction.UP;//向上走
        else direction = Direction.DOWN;

//        System.out.printf("任务创建: 起始位置: %d, 目标位置: %d\n", originFloor, targetFloor);
    }

    public void addWaitingTime(long time) {
        this.waitingTime += time;
    }
    public void addArriveTime(long time) {
        this.arriveTime += time;
    }

    public void finishWait() {
//        System.out.printf("任务完成: 等待: %.2fs\n", (waitingTime / 1000d));
    }

    public void finishElevator() {
//        System.out.printf("任务完成: 乘坐: %.2fs\n", (waitingTime / 1000d));
    }


    /**
     * 乘客初始位置
     */
    public double getTargetFloorHeight(Building building) {
        return (targetFloor - 1) * building.getFloorHeight();
    }

    /**
     * 乘客目标位置
     */
    public double getOriginFloorHeight(Building building) {
        return (targetFloor - 1) * building.getFloorHeight();
    }
}