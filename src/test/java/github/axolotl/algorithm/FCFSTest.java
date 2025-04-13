package github.axolotl.algorithm;

import github.axolotl.elevator.Building;
import github.axolotl.event.ElevatorEvent;
import github.axolotl.event.EventRecoder;
import github.axolotl.event.EventType;
import github.axolotl.passenger.Passenger;
import github.axolotl.passenger.PassengerGenerator;
import github.axolotl.passenger.rule.LimitedFloorRule;
import org.apache.poi.ss.formula.functions.T;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;

/**
 * @author AxolotlXM
 * @Created by Axolotl
 * @Date 2025/4/13 14:01
 */
class FCFSTest {
    @Test
    public void test() {
        // 5F商城
        EventRecoder eventRecoder = new EventRecoder();

        Building building = new Building(2000_000, 5, 3, 100, eventRecoder);

        PassengerGenerator generator = new PassengerGenerator();
        generator.addRule(new LimitedFloorRule(100_000, 200_000, 1, 5));

        Building.Elevator elevator = building.new Elevator(1);
        elevator.setSchedulingAlgorithm(new Algorithm_FCFS());// 设置调度算法

        building.addElevator(elevator);
        building.addPassengerGenerators(generator);

        building.startSimulation();

//        System.out.println("eventRecoder.getEvents() = " + eventRecoder.getEvents());
//        System.out.println("eventRecoder.getEventsByType() = " + eventRecoder.getEventsByType());
        eventRecoder.getEvents()
                .stream()
                .filter(event -> event.getType() == EventType.PassengerFinishElevator)
                .forEach(event -> {
                    ElevatorEvent elevatorEvent = (ElevatorEvent) event;
                    Passenger passenger = elevatorEvent.getPassenger();
                    System.out.printf("""
                                    乘客等待时长: %.2fs
                                    乘客乘坐时长: %.2fs
                                    """
                            ,( passenger.getWaitingTime() / 1000d), (passenger.getArriveTime() / 1000d)
                    );
                });
    }

}