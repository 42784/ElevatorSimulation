package github.axolotl.algorithm;

import github.axolotl.elevator.Building;
import github.axolotl.event.ElevatorEvent;
import github.axolotl.event.EventRecoder;
import github.axolotl.event.EventType;
import github.axolotl.passenger.Passenger;
import github.axolotl.passenger.PassengerGenerator;
import github.axolotl.passenger.rule.LimitedFloorRule;
import github.axolotl.util.ExcelExporter;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * LOOK算法测试类
 *
 * @author AxolotlXM
 * @version 1.0
 * @since 2025/4/15
 */
public class LOOKTest {

    @Test
    public void testLOOK() {
        github.axolotl.model.TestModel.modelA(new Algorithm_FCFS(),"modelA-LOOK");
    }
} 