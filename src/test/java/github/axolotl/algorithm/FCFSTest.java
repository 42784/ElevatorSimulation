package github.axolotl.algorithm;

import org.junit.jupiter.api.Test;


/**
 * @author AxolotlXM
 * @Created by Axolotl
 * @Date 2025/4/13 14:01
 */
class FCFSTest {
    @Test
    public void testFCFS() {
        github.axolotl.model.TestModel.modelA(new Algorithm_FCFS(),"modelA-FCFS");
    }


//        System.out.println("eventRecoder.getEvents() = " + eventRecoder.getEvents());
//        System.out.println("eventRecoder.getEventsByType() = " + eventRecoder.getEventsByType());
//        eventRecoder.getEvents()
//                .stream()
//                .filter(event -> event.getType() == EventType.PassengerFinishElevator)
//                .forEach(event -> {
//                    ElevatorEvent elevatorEvent = (ElevatorEvent) event;
//                    Passenger passenger = elevatorEvent.getPassenger();
//                    System.out.printf("""
//                                    乘客等待时长: %.2fs
//                                    乘客乘坐时长: %.2fs
//                                    """
//                            ,( passenger.getWaitingTime() / 1000d), (passenger.getArriveTime() / 1000d)
//                    );
//                });
}