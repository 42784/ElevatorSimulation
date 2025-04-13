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
import github.axolotl.passenger.rule.PeakTimeMultiFloorRule;
import github.axolotl.passenger.rule.TimeBasedRule;
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
    private static final int INTERVAL = 100;
    //商城模型
    public static void modelA(ElevatorSchedulingAlgorithm schedulingAlgorithm,String fileName) {
        // 创建事件记录器
        EventRecoder eventRecoder = new EventRecoder();
        Building building = new Building(200000_000, 5, 3.0, INTERVAL, eventRecoder);

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
     * 公寓楼高峰期模型 - 多个楼层的居民去往一楼
     * 模拟上班高峰期，多个楼层的居民前往一楼出行
     */
    public static void modelB(ElevatorSchedulingAlgorithm schedulingAlgorithm, String fileName) {
        // 创建事件记录器
        EventRecoder eventRecoder = new EventRecoder();
        // 10层公寓楼
        Building building = new Building(200000_000, 10, 3.0, INTERVAL, eventRecoder);
        
        PassengerGenerator generator = new PassengerGenerator();
        // 上班高峰期：模拟时间从5000万到1亿
        // 高峰期生成频率更高，非高峰期生成频率较低
        // 所有人都去往1楼
        generator.addRule(new PeakTimeMultiFloorRule(
                50000_000, 100000_000,  // 高峰期时间段
                30_000, 60_000,        // 非高峰期间隔
                5_000, 15_000,         // 高峰期间隔
                1));                    // 共同目标楼层(1楼)
        
        Building.Elevator elevator = building.new Elevator(1.5); // 速度稍快的电梯
        elevator.setSchedulingAlgorithm(schedulingAlgorithm);
        
        building.addElevator(elevator);
        building.addPassengerGenerators(generator);
        
        building.startSimulation();
        
        collectData(fileName, eventRecoder);
    }
    
    /**
     * 晚高峰模型 - 一楼去往多个楼层，也有少量相反方向的人流
     */
    public static void modelC(ElevatorSchedulingAlgorithm schedulingAlgorithm, String fileName) {
        // 创建事件记录器
        EventRecoder eventRecoder = new EventRecoder();
        // 15层办公楼
        Building building = new Building(200000_000, 15, 3.0, INTERVAL, eventRecoder);
        
        PassengerGenerator generator = new PassengerGenerator();
        
        // 下班高峰期：从一楼去往多个楼层
        // 高峰期时间段：模拟时间从1亿到1.5亿
        generator.addRule(new TimeBasedRule(
                100000_000, 150000_000,  // 高峰期时间段
                25_000, 50_000,         // 非高峰期间隔
                230_000, 30_000,          // 高峰期间隔
                1, 0,                   // 主要出发楼层(1)和目标楼层(随机，由代码计算)
                0.15));                  // 15%的概率是反向行驶的乘客
        
        Building.Elevator elevator = building.new Elevator(1.2);
        elevator.setSchedulingAlgorithm(schedulingAlgorithm);
        
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
