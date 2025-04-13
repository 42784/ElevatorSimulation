package github.axolotl.event;

import lombok.Getter;

/**
 * @author AxolotlXM
 * @Created by Axolotl
 * @Date 2025/4/13 13:53
 */
public enum EventType {
    PassengerCall("乘客开始等待电梯")
//    , PassengerEvent("乘客数据")

    , PassengerFinishWait("乘客进入电梯")
    ,PassengerFinishElevator("乘客出电梯")
//    ,ElevatorArrive("电梯到达楼层并接走乘客")
    ;
    @Getter
    private final String alias;

    EventType(String alias) {
        this.alias = alias;
    }
}
