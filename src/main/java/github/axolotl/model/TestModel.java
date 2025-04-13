package github.axolotl.model;

import github.axolotl.algorithm.Algorithm_FCFS;
import github.axolotl.algorithm.ElevatorSchedulingAlgorithm;
import github.axolotl.elevator.Building;
import github.axolotl.event.ElevatorEvent;
import github.axolotl.event.EventRecoder;
import github.axolotl.event.EventType;
import github.axolotl.passenger.Passenger;
import github.axolotl.passenger.PassengerGenerator;
import github.axolotl.passenger.rule.LimitedFloorRule;
import github.axolotl.util.ExcelExporter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author AxolotlXM
 * @Created by Axolotl
 * @Date 2025/4/13 16:02
 * 多个测试模型
 */
public class TestModel {
    //商城模型
    public static void modelA(ElevatorSchedulingAlgorithm schedulingAlgorithm,String fileName) {
        // 创建事件记录器
        EventRecoder eventRecoder = new EventRecoder();
        Building building = new Building(200000_000, 5, 3.0, 100, eventRecoder);

        PassengerGenerator generator = new PassengerGenerator();
        generator.addRule(new LimitedFloorRule(10_000, 20_000, 1, 5));

        Building.Elevator elevator = building.new Elevator(1);
        elevator.setSchedulingAlgorithm(schedulingAlgorithm);// 设置调度算法

        building.addElevator(elevator);
        building.addPassengerGenerators(generator);

        building.startSimulation();

        collectData(fileName, eventRecoder);
    }

    /**
     * 收集和导出数据
     */
    private static void collectData(String fileName, EventRecoder eventRecoder) {
        // 收集测试乘客
        List<Passenger> testPassengers;
        testPassengers = eventRecoder.getEvents().stream()
                .filter(event -> event.getType() == EventType.PassengerFinishElevator)
                .map(event -> ((ElevatorEvent) event).getPassenger())
                .toList();


        // 输出统计数据
        System.out.println("\n模拟结束，结果统计：");
        double totalWaitingTime = 0;
        double totalRideTime = 0;

        for (Passenger passenger : testPassengers) {
/*            System.out.printf("乘客从%d楼到%d楼 - 等待时间: %.2f秒, 乘梯时间: %.2f秒\n",
                    passenger.getOriginFloor(), passenger.getTargetFloor(),
                    passenger.getWaitingTime() / 1000.0, passenger.getArriveTime() / 1000.0);*/

            totalWaitingTime += passenger.getWaitingTime();
            totalRideTime += passenger.getArriveTime();
        }

        System.out.printf("\n平均等待时间: %.2f秒\n", totalWaitingTime / 1000.0 / testPassengers.size());
        System.out.printf("平均乘梯时间: %.2f秒\n", totalRideTime / 1000.0 / testPassengers.size());

        // 导出数据
        ExcelExporter.exportPassengerData(testPassengers, "simulation_results/"+ fileName +"_results.xlsx");
        ExcelExporter.exportEventsToTxt(eventRecoder, "simulation_results/"+ fileName +"_events.txt");
    }
}
